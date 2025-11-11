package ru.prohor.universe.venator.webhook

import ru.prohor.universe.venator.webhook.model.WebhookPayload

interface WebhookNotifier {
    fun failure(message: String)

    fun info(message: String)

    fun success(payload: WebhookPayload)
}
