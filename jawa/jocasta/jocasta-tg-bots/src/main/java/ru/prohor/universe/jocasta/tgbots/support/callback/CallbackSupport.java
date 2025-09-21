package ru.prohor.universe.jocasta.tgbots.support.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface CallbackSupport {
    /**
     * @param callback telegram api callback
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handleCallback(CallbackQuery callback, FeedbackExecutor feedbackExecutor);

    static CallbackSupport getUnsupported() {
        return new CallbackUnsupported();
    }
}
