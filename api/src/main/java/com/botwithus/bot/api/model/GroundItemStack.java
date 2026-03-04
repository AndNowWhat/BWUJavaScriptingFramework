package com.botwithus.bot.api.model;

import java.util.List;

public record GroundItemStack(int handle, int tileX, int tileY, int tileZ, List<GroundItem> items) {}
