package com.botwithus.bot.api.model;

public record GameWindowRect(
        int x, int y, int width, int height,
        int clientX, int clientY, int clientWidth, int clientHeight
) {}
