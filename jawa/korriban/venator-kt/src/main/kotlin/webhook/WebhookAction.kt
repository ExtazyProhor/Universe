package ru.prohor.universe.venator.webhook

import ru.prohor.universe.venator.webhook.model.WebhookPayload

interface WebhookAction {
    fun accept(payload: WebhookPayload)
}
