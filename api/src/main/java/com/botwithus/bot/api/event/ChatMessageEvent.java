package com.botwithus.bot.api.event;

import com.botwithus.bot.api.model.ChatMessage;

public class ChatMessageEvent extends GameEvent {
    private final ChatMessage message;

    public ChatMessageEvent(ChatMessage message) {
        super("chat_message");
        this.message = message;
    }

    public ChatMessage getMessage() { return message; }
}
