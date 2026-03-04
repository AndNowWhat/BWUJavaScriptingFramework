package com.botwithus.bot.api.model;

public record GrandExchangeOffer(
        int slot, int status, int type, int itemId,
        int price, int count, int completedCount, int completedGold
) {}
