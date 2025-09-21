package ru.prohor.universe.jocasta.tgbots.api;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface FeedbackExecutor {
    void sendMessage(SendMessage message);

    void editMessageText(EditMessageText message);
}
