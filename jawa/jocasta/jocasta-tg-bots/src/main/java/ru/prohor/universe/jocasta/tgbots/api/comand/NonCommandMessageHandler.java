package ru.prohor.universe.jocasta.tgbots.api.comand;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface NonCommandMessageHandler {
    void onNonCommandMessage(Message message, FeedbackExecutor feedbackExecutor);
}
