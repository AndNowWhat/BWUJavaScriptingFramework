package com.botwithus.bot.core.impl;

import com.botwithus.bot.api.event.EventBus;
import com.botwithus.bot.api.event.GameEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBusImpl implements EventBus {

    private final Map<Class<? extends GameEvent>, List<Consumer<? extends GameEvent>>> listeners = new ConcurrentHashMap<>();

    @Override
    public <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    public <T extends GameEvent> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<? extends GameEvent>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publish(GameEvent event) {
        List<Consumer<? extends GameEvent>> list = listeners.get(event.getClass());
        if (list != null) {
            for (Consumer<? extends GameEvent> listener : list) {
                ((Consumer<GameEvent>) listener).accept(event);
            }
        }
    }
}
