package com.botwithus.bot.api.model;

public record LocalPlayer(
        int serverIndex, String name,
        int tileX, int tileY, int plane,
        boolean isMember, boolean isMoving,
        int animationId, int stanceId,
        int health, int maxHealth, int combatLevel,
        String overheadText, int targetIndex, int targetType
) {}
