package com.botwithus.bot.api.model;

public record EntityInfo(
        int handle, int serverIndex, int typeId,
        int tileX, int tileY, int tileZ,
        String name, int nameHash,
        boolean isMoving, boolean isHidden,
        int health, int maxHealth,
        int animationId, int stanceId,
        int followingIndex,
        String overheadText, int combatLevel
) {}
