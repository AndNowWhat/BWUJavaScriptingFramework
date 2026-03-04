package com.botwithus.bot.api.model;

public record ViewportInfo(
        int viewportWidth, int viewportHeight,
        float[] projectionMatrix, float[] viewMatrix
) {}
