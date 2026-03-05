package com.botwithus.bot.api.isc;

import java.util.function.Consumer;

/**
 * Async pub/sub message bus for inter-script communication over named channels.
 */
public interface MessageBus {

    /**
     * Subscribes a handler to receive messages on the given channel.
     *
     * @param channel the channel name to subscribe to
     * @param handler the callback invoked for each message on the channel
     */
    void subscribe(String channel, Consumer<ScriptMessage> handler);

    /**
     * Removes a previously registered handler from the given channel.
     *
     * @param channel the channel name
     * @param handler the handler to remove
     */
    void unsubscribe(String channel, Consumer<ScriptMessage> handler);

    /**
     * Publishes a message to all subscribers of the given channel.
     * Delivery is asynchronous; the caller never blocks.
     *
     * @param channel the channel to publish on
     * @param sender  identifier of the sending script
     * @param payload the message payload
     */
    void publish(String channel, String sender, Object payload);
}
