package com.botwithus.bot.api.isc;

/**
 * An immutable message sent over a named ISC channel.
 *
 * @param channel   the channel the message was published on
 * @param sender    identifier of the sending script
 * @param payload   the message payload
 * @param timestamp epoch millis when the message was created
 */
public record ScriptMessage(String channel, String sender, Object payload, long timestamp) {
}
