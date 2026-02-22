package ru.prohor.universe.bobafett.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;

@Service
public class UnknownCallbackPrefixHandler implements UnknownActionKeyHandler<CallbackQuery, String> {
    private static final Logger log = LoggerFactory.getLogger(UnknownCallbackPrefixHandler.class);

    @Override
    public void handleUnknownActionKey(CallbackQuery callback, String data, FeedbackExecutor feedbackExecutor) {
        log.error("unknown callback '{}' from chat with id {}", data, callback.getMessage().getChatId());
    }
}
