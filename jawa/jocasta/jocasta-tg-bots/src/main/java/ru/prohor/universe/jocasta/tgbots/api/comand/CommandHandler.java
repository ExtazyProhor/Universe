package ru.prohor.universe.jocasta.tgbots.api.comand;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface CommandHandler extends ActionHandler<String> {
    /**
     * @return command with a leading slash, for example, <code>/start</code>
     */
    String command();

    @Override
    default String key() {
        return command();
    }

    /**
     * @return command description
     */
    String description();

    /**
     * @param message          telegram api message
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(Message message, FeedbackExecutor feedbackExecutor);
}
