package ru.prohor.universe.jocasta.tgbots.api;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface FeedbackExecutor {
    void sendMessage(SendMessage message);

    void editMessageText(EditMessageText message);

    default void sendMessage(Long chatId, String text) {
        sendMessage(SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build());
    }
}
