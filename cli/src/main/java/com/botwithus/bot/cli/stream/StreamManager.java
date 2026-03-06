package com.botwithus.bot.cli.stream;

import com.botwithus.bot.cli.Connection;
import com.botwithus.bot.cli.gui.StreamWindow;
import com.botwithus.bot.core.pipe.StreamPipeReader;

import javax.swing.*;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Manages active video streams: starting/stopping stream pipes, and
 * coordinating the StreamWindow display.
 */
public class StreamManager {

    private record ActiveStream(StreamPipeReader reader, StreamWindow.StreamCell cell) {}

    private final Map<String, ActiveStream> streams = new LinkedHashMap<>();
    private StreamWindow window;
    private final PrintStream out;

    public StreamManager(PrintStream out) {
        this.out = out;
    }

    /**
     * Start streaming for a single connection.
     *
     * @param conn      the active connection
     * @param quality   JPEG quality (0-100)
     * @param frameSkip frames to skip between sends (higher = lower fps)
     * @param width     stream width in pixels
     * @param height    stream height in pixels
     */
    public void startStream(Connection conn, int quality, int frameSkip, int width, int height) {
        String name = conn.getName();
        if (streams.containsKey(name)) {
            out.println("Already streaming '" + name + "'.");
            return;
        }

        // Call start_stream RPC
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("quality", quality);
        params.put("frame_skip", frameSkip);
        params.put("width", width);
        params.put("height", height);

        Map<String, Object> result;
        try {
            result = conn.getRpc().callSync("start_stream", params);
        } catch (Exception e) {
            out.println("Failed to start stream for '" + name + "': " + e.getMessage());
            return;
        }

        out.println("start_stream response: " + result);

        Object pipeNameObj = result.get("pipe_name");
        if (pipeNameObj == null) {
            out.println("Server did not return 'pipe_name' for '" + name + "'. Keys: " + result.keySet());
            return;
        }
        String streamPipeName = pipeNameObj.toString();
        out.println("Stream pipe name: " + streamPipeName);

        // Ensure window exists on EDT
        ensureWindow();

        // Add cell on EDT and start reader
        SwingUtilities.invokeLater(() -> {
            StreamWindow.StreamCell cell = window.addCell(name);

            StreamPipeReader reader = new StreamPipeReader(streamPipeName, cell::updateFrame);
            reader.setErrorCallback(out::println);
            streams.put(name, new ActiveStream(reader, cell));
            reader.start();

            window.setVisible(true);
            out.println("Streaming '" + name + "' from pipe: " + streamPipeName);
        });
    }

    /**
     * Stop streaming for a single connection.
     */
    public void stopStream(String connectionName, Function<String, Connection> connectionLookup) {
        ActiveStream active = streams.remove(connectionName);
        if (active == null) {
            out.println("No active stream for '" + connectionName + "'.");
            return;
        }

        active.reader.close();

        // Send stop_stream RPC (best-effort)
        Connection conn = connectionLookup.apply(connectionName);
        if (conn != null && conn.isAlive()) {
            try {
                conn.getRpc().callSync("stop_stream", Map.of());
            } catch (Exception ignored) {}
        }

        SwingUtilities.invokeLater(() -> {
            if (window != null) {
                window.removeCell(connectionName);
                if (!window.hasCells()) {
                    window.dispose();
                    window = null;
                }
            }
        });

        out.println("Stopped stream for '" + connectionName + "'.");
    }

    /**
     * Stop all active streams and dispose the window.
     */
    public void stopAll(Function<String, Connection> connectionLookup) {
        for (var entry : Map.copyOf(streams).entrySet()) {
            String name = entry.getKey();
            ActiveStream active = entry.getValue();
            active.reader.close();

            // Best-effort stop_stream RPC
            if (connectionLookup != null) {
                Connection conn = connectionLookup.apply(name);
                if (conn != null && conn.isAlive()) {
                    try {
                        conn.getRpc().callSync("stop_stream", Map.of());
                    } catch (Exception ignored) {}
                }
            }
        }
        streams.clear();

        SwingUtilities.invokeLater(() -> {
            if (window != null) {
                window.dispose();
                window = null;
            }
        });
    }

    /**
     * Handle a connection being lost — clean up its stream if active.
     */
    public void handleConnectionLost(String connectionName) {
        ActiveStream active = streams.remove(connectionName);
        if (active == null) return;

        active.reader.close();

        SwingUtilities.invokeLater(() -> {
            if (window != null) {
                window.removeCell(connectionName);
                if (!window.hasCells()) {
                    window.dispose();
                    window = null;
                }
            }
        });
    }

    public boolean hasActiveStreams() {
        return !streams.isEmpty();
    }

    private void ensureWindow() {
        if (window == null) {
            // Create window on EDT but wait for it
            try {
                SwingUtilities.invokeAndWait(() -> {
                    window = new StreamWindow();
                    window.setOnCloseCallback(() -> stopAll(null));
                });
            } catch (Exception e) {
                throw new RuntimeException("Failed to create stream window", e);
            }
        }
    }
}
