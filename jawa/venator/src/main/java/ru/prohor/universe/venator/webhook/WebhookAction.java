package ru.prohor.universe.venator.webhook;

public interface WebhookAction {
    void accept(WebhookPayload payload) throws Exception;
}
