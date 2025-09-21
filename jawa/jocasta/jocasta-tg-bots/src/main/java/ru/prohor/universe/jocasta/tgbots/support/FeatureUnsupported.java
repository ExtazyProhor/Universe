package ru.prohor.universe.jocasta.tgbots.support;

import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public class FeatureUnsupported<T> implements FeatureSupport<T> {
    @Override
    public boolean handle(T payload, FeedbackExecutor feedbackExecutor) {
        return true;
    }
}
