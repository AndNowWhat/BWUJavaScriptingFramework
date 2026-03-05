package com.botwithus.bot.core.pipe;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Named pipe client connecting to \\.\pipe\BotWithUs.
 * Uses 4-byte LE length-prefix framing.
 *
 * <p>All I/O is synchronous. Windows named pipes use non-overlapped handles,
 * so concurrent read/write on the same handle causes deadlocks. Both
 * {@link #send} and {@link #readMessage} are synchronized on this instance
 * to ensure only one I/O operation at a time.</p>
 */
public class PipeClient implements AutoCloseable {

    private static final String PIPE_PREFIX = "\\\\.\\pipe\\";
    private static final String DEFAULT_PIPE_NAME = "BotWithUs";

    private final String pipePath;
    private final RandomAccessFile pipe;
    private volatile boolean open = true;

    public PipeClient() {
        this(DEFAULT_PIPE_NAME);
    }

    public PipeClient(String pipeName) {
        this.pipePath = PIPE_PREFIX + pipeName;
        try {
            this.pipe = new RandomAccessFile(pipePath, "rw");
        } catch (IOException e) {
            throw new PipeException("Failed to connect to pipe: " + pipePath, e);
        }
    }

    public static List<String> scanPipes() {
        return scanPipes("BotWithUs");
    }

    public static List<String> scanPipes(String prefix) {
        String lowerPrefix = prefix.toLowerCase();
        try (Stream<Path> stream = Files.list(Path.of(PIPE_PREFIX))) {
            return stream
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.toLowerCase().contains(lowerPrefix))
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    public String getPipePath() {
        return pipePath;
    }

    public boolean isOpen() {
        return open;
    }

    /**
     * Sends a length-prefixed message over the pipe.
     */
    public synchronized void send(byte[] data) {
        if (!open) throw new PipeException("Pipe is closed");
        try {
            byte[] header = ByteBuffer.allocate(4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(data.length)
                    .array();
            pipe.write(header);
            pipe.write(data);
        } catch (IOException e) {
            throw new PipeException("Failed to send message", e);
        }
    }

    /**
     * Reads the next length-prefixed message from the pipe.
     * Blocks until a complete message is available.
     */
    public synchronized byte[] readMessage() {
        if (!open) throw new PipeException("Pipe is closed");
        try {
            byte[] header = new byte[4];
            readFully(header);
            int length = ByteBuffer.wrap(header)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .getInt();
            if (length <= 0 || length > 16 * 1024 * 1024) {
                throw new PipeException("Invalid message length: " + length);
            }
            byte[] payload = new byte[length];
            readFully(payload);
            return payload;
        } catch (IOException e) {
            throw new PipeException("Pipe read error", e);
        }
    }

    private void readFully(byte[] buf) throws IOException {
        int off = 0;
        while (off < buf.length) {
            int n = pipe.read(buf, off, buf.length - off);
            if (n < 0) throw new IOException("Pipe closed");
            off += n;
        }
    }

    @Override
    public void close() {
        open = false;
        try { pipe.close(); } catch (IOException ignored) {}
    }
}
