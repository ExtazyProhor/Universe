package ru.prohor.universe.jocasta.tgbots.api.status;

import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface StatusHandler<K, V> extends ActionHandler<K> {
    /**
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(V statusValue, FeedbackExecutor feedbackExecutor);
}
