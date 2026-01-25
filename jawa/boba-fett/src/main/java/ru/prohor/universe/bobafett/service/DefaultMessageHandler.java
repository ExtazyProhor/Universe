package ru.prohor.universe.bobafett.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.NonCommandMessageHandler;

@Service
public class DefaultMessageHandler implements NonCommandMessageHandler {
    @Override
    public void onNonCommandMessage(Message message, FeedbackExecutor feedbackExecutor) {}
}
