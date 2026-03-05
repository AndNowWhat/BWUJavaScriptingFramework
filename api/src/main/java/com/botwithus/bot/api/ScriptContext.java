package com.botwithus.bot.api;

import com.botwithus.bot.api.event.EventBus;
import com.botwithus.bot.api.isc.MessageBus;

/**
 * Context object passed to {@link BotScript#onStart} providing access to
 * the game API and the event bus.
 *
 * @see BotScript
 * @see GameAPI
 * @see EventBus
 * @see MessageBus
 */
public interface ScriptContext {

    /**
     * Returns the game API for interacting with the game client.
     *
     * @return the {@link GameAPI} instance
     */
    GameAPI getGameAPI();

    /**
     * Returns the event bus for subscribing to and publishing game events.
     *
     * @return the {@link EventBus} instance
     */
    EventBus getEventBus();

    /**
     * Returns the message bus for inter-script communication.
     *
     * @return the {@link MessageBus} instance
     */
    MessageBus getMessageBus();
}
