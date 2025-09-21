package ru.prohor.universe.jocasta.tgbots.support.status;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface StatusSupport {
    /**
     * @param update telegram api update
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handleUpdate(Update update, FeedbackExecutor feedbackExecutor);

    static StatusSupport getUnsupported() {
        return new StatusUnsupported();
    }
}
