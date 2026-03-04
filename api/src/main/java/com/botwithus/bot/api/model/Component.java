package com.botwithus.bot.api.model;

public record Component(
        int handle, int interfaceId, int componentId, int subComponentId,
        int type, int itemId, int itemCount, int spriteId
) {}
