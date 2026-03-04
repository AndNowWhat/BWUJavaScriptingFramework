package com.botwithus.bot.api.model;

public record EntityScreenPosition(
        int handle, double screenX, double screenY, boolean valid
) {}
