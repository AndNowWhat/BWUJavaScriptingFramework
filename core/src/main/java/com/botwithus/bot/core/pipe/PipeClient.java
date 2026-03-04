package com.botwithus.bot.core.pipe;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

/**
 * Named pipe client connecting to \\.\pipe\BotWithUs.
 * Uses 4-byte LE length-prefix framing.
 */
public class PipeClient implements AutoCloseable {

    private static final String PIPE_PATH = "\\\\.\\pipe\\BotWithUs";

    private final RandomAccessFile pipe;
    private final InputStream in;
    private final OutputStream out;
    private final Thread readerThread;
    private volatile boolean running = true;
    private Consumer<byte[]> messageHandler;

    public PipeClient() {
        try {
            this.pipe = new RandomAccessFile(PIPE_PATH, "rw");
            this.in = new FileInputStream(pipe.getFD());
            this.out = new FileOutputStream(pipe.getFD());
        } catch (IOException e) {
            throw new PipeException("Failed to connect to pipe: " + PIPE_PATH, e);
        }

        this.readerThread = Thread.ofVirtual().name("pipe-reader").start(this::readLoop);
    }

    public void setMessageHandler(Consumer<byte[]> handler) {
        this.messageHandler = handler;
    }

    public synchronized void send(byte[] data) {
        try {
            byte[] header = ByteBuffer.allocate(4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(data.length)
                    .array();
            out.write(header);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new PipeException("Failed to send message", e);
        }
    }

    private void readLoop() {
        try {
            byte[] headerBuf = new byte[4];
            while (running) {
                readFully(headerBuf);
                int length = ByteBuffer.wrap(headerBuf)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .getInt();
                if (length <= 0 || length > 16 * 1024 * 1024) {
                    throw new PipeException("Invalid message length: " + length);
                }
                byte[] payload = new byte[length];
                readFully(payload);

                Consumer<byte[]> handler = this.messageHandler;
                if (handler != null) {
                    handler.accept(payload);
                }
            }
        } catch (IOException e) {
            if (running) {
                throw new PipeException("Pipe read error", e);
            }
        }
    }

    private void readFully(byte[] buf) throws IOException {
        int off = 0;
        while (off < buf.length) {
            int n = in.read(buf, off, buf.length - off);
            if (n < 0) throw new IOException("Pipe closed");
            off += n;
        }
    }

    @Override
    public void close() {
        running = false;
        readerThread.interrupt();
        try { pipe.close(); } catch (IOException ignored) {}
    }
}
