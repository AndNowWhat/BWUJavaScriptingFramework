package com.botwithus.bot.core.rpc;

import com.botwithus.bot.core.msgpack.MessagePackCodec;
import com.botwithus.bot.core.pipe.PipeClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * RPC layer over the named pipe. Sends a request then reads messages until
 * the matching response arrives — all on the calling thread.
 *
 * <p>Windows named pipes use non-overlapped handles where all I/O is
 * serialized by the kernel. A dedicated reader thread would deadlock with
 * a writer thread, so we do sequential write-then-read instead.</p>
 *
 * <p>If the server pushes event messages before the RPC response, they are
 * dispatched to the {@link #setEventHandler event handler} inline.</p>
 */
public class RpcClient implements AutoCloseable {

    private final PipeClient pipe;
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private Consumer<Map<String, Object>> eventHandler;

    public RpcClient(PipeClient pipe) {
        this.pipe = pipe;
    }

    public void setEventHandler(Consumer<Map<String, Object>> handler) {
        this.eventHandler = handler;
    }

    /**
     * Synchronous RPC call. Returns the {@code "result"} field as a Map.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> callSync(String method, Map<String, Object> params) {
        Map<String, Object> response = doCall(method, params);

        if (response.containsKey("error") && response.get("error") != null) {
            throw new RpcException("RPC error: " + response.get("error"));
        }
        Object result = response.get("result");
        if (result instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return Map.of("value", result != null ? result : Map.of());
    }

    /**
     * Synchronous call that returns the raw result value (may be Map, List, or primitive).
     */
    public Object callSyncRaw(String method, Map<String, Object> params) {
        Map<String, Object> response = doCall(method, params);

        if (response.containsKey("error") && response.get("error") != null) {
            throw new RpcException("RPC error: " + response.get("error"));
        }
        return response.get("result");
    }

    /**
     * Synchronous call for methods that return an array result.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> callSyncList(String method, Map<String, Object> params) {
        Object raw = callSyncRaw(method, params);
        if (raw instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    @Override
    public void close() {
        pipe.close();
    }

    // ========================== Internal ==========================

    /**
     * Sends the request, then reads messages until the response with the
     * matching ID arrives. Events received in between are dispatched.
     */
    private synchronized Map<String, Object> doCall(String method, Map<String, Object> params) {
        int id = idCounter.getAndIncrement();

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("method", method);
        request.put("id", id);
        if (params != null && !params.isEmpty()) {
            request.put("params", params);
        }

        try {
            pipe.send(MessagePackCodec.encode(request));

            // Read messages until we get the response matching our request ID.
            // Any event messages that arrive first are dispatched inline.
            while (true) {
                byte[] responseBytes = pipe.readMessage();
                Map<String, Object> msg = MessagePackCodec.decode(responseBytes);

                if (msg.containsKey("event")) {
                    dispatchEvent(msg);
                    continue;
                }

                if (matchesId(msg, id)) {
                    return msg;
                }

                // Unknown message (no event, wrong id) — skip it
            }
        } catch (RpcException e) {
            throw e;
        } catch (Exception e) {
            throw new RpcException("RPC call failed: " + method, e);
        }
    }

    private boolean matchesId(Map<String, Object> msg, int expectedId) {
        Object idObj = msg.get("id");
        if (idObj instanceof Number n) return n.intValue() == expectedId;
        return false;
    }

    private void dispatchEvent(Map<String, Object> msg) {
        Consumer<Map<String, Object>> handler = this.eventHandler;
        if (handler != null) {
            handler.accept(msg);
        }
    }
}
