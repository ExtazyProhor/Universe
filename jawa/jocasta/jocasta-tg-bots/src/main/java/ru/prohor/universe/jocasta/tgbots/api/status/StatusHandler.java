package ru.prohor.universe.jocasta.tgbots.api.status;

import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface StatusHandler<StatusKey, StatusValue> {
    StatusKey key();

    /**
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(StatusValue statusValue, FeedbackExecutor feedbackExecutor);
}
