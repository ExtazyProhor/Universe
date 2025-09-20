package ru.prohor.universe.jocasta.tgbots.api.status;

public interface StatusHandler<StatusKey, StatusValue> {
    StatusKey key();

    boolean shouldContinueProcessing();

    void handle(StatusValue statusValue);
}
