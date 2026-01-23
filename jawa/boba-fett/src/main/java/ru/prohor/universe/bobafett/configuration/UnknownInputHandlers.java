package ru.prohor.universe.bobafett.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.service.ObjectsEncoder;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

@Service
public class UnknownInputHandlers {
    private static final Logger log = LoggerFactory.getLogger(UnknownInputHandlers.class);

    private final ObjectsEncoder objectsEncoder;

    public UnknownInputHandlers(ObjectsEncoder objectsEncoder) {
        this.objectsEncoder = objectsEncoder;
    }

    public boolean handleUnknownCommand(Message message, String command, FeedbackExecutor feedbackExecutor) {
        log.trace("unknown command '{}' from chat with id {}", command, message.getChatId());
        feedbackExecutor.sendMessage(
                message.getChatId(),
                "Неизвестная команда. Посмотреть список доступных команд - /commands"
        );
        return false;
    }

    public boolean handleUnknownCallback(
            CallbackQuery callback,
            String data,
            @SuppressWarnings("unused") FeedbackExecutor feedbackExecutor
    ) {
        log.error("unknown callback '{}' from chat with id {}", data, callback.getMessage().getChatId());
        return false;
    }

    public boolean handleUnknownStatus(
            Update update,
            String statusKey,
            @SuppressWarnings("unused") FeedbackExecutor feedbackExecutor
    ) {
        log.error("unknown status '{}'. Full update - base64('{}')", statusKey, objectsEncoder.encode(update));
        return false;
    }
}
