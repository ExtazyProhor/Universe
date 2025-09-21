package ru.prohor.universe.jocasta.tgbots.support.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface CommandSupport {
    /**
     * @param message telegram api message
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handleMessage(Message message, FeedbackExecutor feedbackExecutor);

    static CommandSupport getUnsupported() {
        return new CommandUnsupported();
    }
}
