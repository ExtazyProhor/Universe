package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusFlow;

public interface StatusSupport {
    /**
     * @param update           telegram api update to be processed
     * @param feedbackExecutor interface for sending feedback to users
     */
    StatusFlow handle(Update update, FeedbackExecutor feedbackExecutor);
}
