package ru.prohor.universe.venator.webhook;

import ru.prohor.universe.venator.webhook.model.WebhookPayload;

public interface WebhookAction {
    void accept(WebhookPayload payload) throws Exception;
}
