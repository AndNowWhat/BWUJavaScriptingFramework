package com.botwithus.bot.core.impl;

import com.botwithus.bot.api.isc.MessageBus;
import com.botwithus.bot.api.isc.ScriptMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class MessageBusImpl implements MessageBus {

    private final Map<String, List<Consumer<ScriptMessage>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String channel, Consumer<ScriptMessage> handler) {
        subscribers.computeIfAbsent(channel, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public void unsubscribe(String channel, Consumer<ScriptMessage> handler) {
        List<Consumer<ScriptMessage>> list = subscribers.get(channel);
        if (list != null) {
            list.remove(handler);
        }
    }

    @Override
    public void publish(String channel, String sender, Object payload) {
        List<Consumer<ScriptMessage>> list = subscribers.get(channel);
        if (list == null || list.isEmpty()) {
            return;
        }
        ScriptMessage message = new ScriptMessage(channel, sender, payload, System.currentTimeMillis());
        for (Consumer<ScriptMessage> handler : list) {
            Thread.startVirtualThread(() -> handler.accept(message));
        }
    }
}
