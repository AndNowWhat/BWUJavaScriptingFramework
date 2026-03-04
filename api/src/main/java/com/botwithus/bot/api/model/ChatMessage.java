package com.botwithus.bot.api.model;

public record ChatMessage(int index, int messageType, String text, String playerName) {}
