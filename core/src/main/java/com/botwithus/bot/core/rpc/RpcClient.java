package com.botwithus.bot.core.rpc;

import com.botwithus.bot.core.msgpack.MessagePackCodec;
import com.botwithus.bot.core.pipe.PipeClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * RPC layer over the named pipe. Manages request IDs, correlates responses,
 * and routes events to a handler.
 */
public class RpcClient implements AutoCloseable {

    private static final int DEFAULT_TIMEOUT_MS = 5000;

    private final PipeClient pipe;
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final ConcurrentHashMap<Integer, CompletableFuture<Map<String, Object>>> pending = new ConcurrentHashMap<>();
    private Consumer<Map<String, Object>> eventHandler;

    public RpcClient(PipeClient pipe) {
        this.pipe = pipe;
        this.pipe.setMessageHandler(this::onMessage);
    }

    public void setEventHandler(Consumer<Map<String, Object>> handler) {
        this.eventHandler = handler;
    }

    public CompletableFuture<Map<String, Object>> call(String method, Map<String, Object> params) {
        int id = idCounter.getAndIncrement();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        pending.put(id, future);

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("method", method);
        request.put("id", id);
        if (params != null && !params.isEmpty()) {
            request.put("params", params);
        }

        try {
            pipe.send(MessagePackCodec.encode(request));
        } catch (Exception e) {
            pending.remove(id);
            future.completeExceptionally(new RpcException("Failed to send RPC request", e));
        }

        return future;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> callSync(String method, Map<String, Object> params) {
        try {
            Map<String, Object> response = call(method, params).get(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (response.containsKey("error") && response.get("error") != null) {
                throw new RpcException("RPC error: " + response.get("error"));
            }
            Object result = response.get("result");
            if (result instanceof Map<?, ?> m) {
                return (Map<String, Object>) m;
            }
            // Wrap non-map results
            return Map.of("value", result != null ? result : Map.of());
        } catch (TimeoutException e) {
            throw new RpcException("RPC call timed out: " + method, e);
        } catch (RpcException e) {
            throw e;
        } catch (Exception e) {
            throw new RpcException("RPC call failed: " + method, e);
        }
    }

    /**
     * Synchronous call that returns the raw result value (may be Map, List, or primitive).
     */
    public Object callSyncRaw(String method, Map<String, Object> params) {
        try {
            Map<String, Object> response = call(method, params).get(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (response.containsKey("error") && response.get("error") != null) {
                throw new RpcException("RPC error: " + response.get("error"));
            }
            return response.get("result");
        } catch (TimeoutException e) {
            throw new RpcException("RPC call timed out: " + method, e);
        } catch (RpcException e) {
            throw e;
        } catch (Exception e) {
            throw new RpcException("RPC call failed: " + method, e);
        }
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

    @SuppressWarnings("unchecked")
    private void onMessage(byte[] data) {
        Map<String, Object> msg = MessagePackCodec.decode(data);

        // Event messages have an "event" key
        if (msg.containsKey("event")) {
            Consumer<Map<String, Object>> handler = this.eventHandler;
            if (handler != null) {
                handler.accept(msg);
            }
            return;
        }

        // Response messages have an "id" key
        if (msg.containsKey("id")) {
            Object idObj = msg.get("id");
            int id = (idObj instanceof Number n) ? n.intValue() : Integer.parseInt(idObj.toString());
            CompletableFuture<Map<String, Object>> future = pending.remove(id);
            if (future != null) {
                future.complete(msg);
            }
        }
    }

    @Override
    public void close() {
        pipe.close();
        pending.values().forEach(f -> f.completeExceptionally(new RpcException("Client closed")));
        pending.clear();
    }
}
