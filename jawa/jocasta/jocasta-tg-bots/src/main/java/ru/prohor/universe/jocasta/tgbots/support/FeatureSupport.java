package ru.prohor.universe.jocasta.tgbots.support;

import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

/**
 * @param <T> object from telegram api to be processed
 */
public interface FeatureSupport<T> {
    /**
     * @param payload          telegram api object to be processed
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(T payload, FeedbackExecutor feedbackExecutor);
}
