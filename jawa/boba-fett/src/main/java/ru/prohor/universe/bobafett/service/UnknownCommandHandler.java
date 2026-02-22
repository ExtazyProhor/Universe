package ru.prohor.universe.bobafett.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;

@Service
public class UnknownCommandHandler implements UnknownActionKeyHandler<Message, String> {
    private static final Logger log = LoggerFactory.getLogger(UnknownCommandHandler.class);

    @Override
    public void handleUnknownActionKey(Message message, String command, FeedbackExecutor feedbackExecutor) {
        log.trace("unknown command '{}' from chat with id {}", command, message.getChatId());
        feedbackExecutor.sendMessage(
                message.getChatId(),
                "Неизвестная команда. Посмотреть список доступных команд - " + Commands.COMMANDS
        );
    }
}
