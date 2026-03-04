package com.botwithus.bot.api.event;

public class GameEvent {
    private final String type;
    private final long timestamp;

    public GameEvent(String type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public GameEvent(String type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public long getTimestamp() { return timestamp; }
}
