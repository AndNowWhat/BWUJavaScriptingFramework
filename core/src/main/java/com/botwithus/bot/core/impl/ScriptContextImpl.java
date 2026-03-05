package com.botwithus.bot.core.impl;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.ScriptContext;
import com.botwithus.bot.api.event.EventBus;
import com.botwithus.bot.api.isc.MessageBus;

public class ScriptContextImpl implements ScriptContext {

    private final GameAPI gameAPI;
    private final EventBus eventBus;
    private final MessageBus messageBus;

    public ScriptContextImpl(GameAPI gameAPI, EventBus eventBus, MessageBus messageBus) {
        this.gameAPI = gameAPI;
        this.eventBus = eventBus;
        this.messageBus = messageBus;
    }

    @Override
    public GameAPI getGameAPI() { return gameAPI; }

    @Override
    public EventBus getEventBus() { return eventBus; }

    @Override
    public MessageBus getMessageBus() { return messageBus; }
}
