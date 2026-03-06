package com.botwithus.bot.api;

import com.botwithus.bot.api.event.EventBus;

/**
 * Represents a single connected game client with its associated API and event bus.
 */
public interface Client {

    String getName();

    GameAPI getGameAPI();

    EventBus getEventBus();

    boolean isConnected();
}
