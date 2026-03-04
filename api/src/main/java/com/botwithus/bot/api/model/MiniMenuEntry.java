package com.botwithus.bot.api.model;

public record MiniMenuEntry(
        String optionText, int actionId, int typeId,
        int itemId, int param1, int param2, int param3
) {}
