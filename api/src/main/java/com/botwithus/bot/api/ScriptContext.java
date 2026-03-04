package com.botwithus.bot.api;

import com.botwithus.bot.api.event.EventBus;

public interface ScriptContext {
    GameAPI getGameAPI();
    EventBus getEventBus();
}
