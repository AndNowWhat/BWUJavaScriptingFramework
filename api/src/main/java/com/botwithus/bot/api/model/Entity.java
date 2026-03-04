package com.botwithus.bot.api.model;

public record Entity(
        int handle, int serverIndex, int typeId,
        int tileX, int tileY, int tileZ,
        String name, int nameHash,
        boolean isMoving, boolean isHidden
) {}
