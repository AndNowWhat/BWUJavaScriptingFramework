package com.botwithus.bot.api.event;

import java.util.function.Consumer;

public interface EventBus {
    <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener);
    <T extends GameEvent> void unsubscribe(Class<T> eventType, Consumer<T> listener);
    void publish(GameEvent event);
}
