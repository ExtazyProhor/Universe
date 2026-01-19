package ru.prohor.universe.jocasta.tgbots.api;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface FeedbackExecutor {
    void sendMessage(SendMessage message);

    void editMessageText(EditMessageText message);

    void sendDocument(SendDocument document);

    default void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        sendMessage(sendMessage);
    }

    default void editMessageText(Long chatId, Integer messageId, String text) {
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .build();
        editMessageText(editMessageText);
    }

    default void editMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .replyMarkup(keyboard)
                .build();
        editMessageText(editMessageText);
    }

    default void sendDocument(Long chatId, String content, String fileName, String caption) {
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        InputFile inputFile = new InputFile(inputStream, fileName);
        SendDocument document = SendDocument.builder()
                .chatId(chatId)
                .document(inputFile)
                .caption(caption)
                .build();
        sendDocument(document);
    }
}
