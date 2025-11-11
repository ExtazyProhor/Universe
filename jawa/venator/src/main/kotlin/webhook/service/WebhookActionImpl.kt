package ru.prohor.universe.venator.webhook.service

import org.springframework.stereotype.Service
import ru.prohor.universe.venator.webhook.WebhookAction
import ru.prohor.universe.venator.webhook.model.WebhookPayload

@Service
class WebhookActionImpl : WebhookAction {
    override fun accept(payload: WebhookPayload) {
        println(payload)
    }
}
