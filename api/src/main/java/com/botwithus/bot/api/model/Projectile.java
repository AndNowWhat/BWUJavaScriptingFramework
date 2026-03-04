package com.botwithus.bot.api.model;

public record Projectile(
        int handle, int projectileId,
        int startX, int startY, int endX, int endY, int plane,
        int targetIndex, int sourceIndex,
        int startCycle, int endCycle
) {}
