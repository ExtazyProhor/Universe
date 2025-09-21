package ru.prohor.universe.jocasta.tgbots.api.comand;

import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface CommandHandler {
    /**
     * @return command with a leading slash, for example, <code>/start</code>
     */
    String command();

    String description();

    /**
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(FeedbackExecutor feedbackExecutor);
}
