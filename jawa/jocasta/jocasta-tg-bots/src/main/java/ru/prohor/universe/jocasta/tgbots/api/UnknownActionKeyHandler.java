package ru.prohor.universe.jocasta.tgbots.api;

public interface UnknownActionKeyHandler<T, K> {
    void handleUnknownActionKey(T actionObject, K key, FeedbackExecutor feedbackExecutor);
}
