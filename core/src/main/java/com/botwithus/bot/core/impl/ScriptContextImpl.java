package com.botwithus.bot.core.impl;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.ScriptContext;
import com.botwithus.bot.api.event.EventBus;

public class ScriptContextImpl implements ScriptContext {

    private final GameAPI gameAPI;
    private final EventBus eventBus;

    public ScriptContextImpl(GameAPI gameAPI, EventBus eventBus) {
        this.gameAPI = gameAPI;
        this.eventBus = eventBus;
    }

    @Override
    public GameAPI getGameAPI() { return gameAPI; }

    @Override
    public EventBus getEventBus() { return eventBus; }
}
