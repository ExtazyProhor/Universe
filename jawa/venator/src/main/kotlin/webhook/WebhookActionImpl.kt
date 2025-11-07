package ru.prohor.universe.venator.webhook

import org.springframework.stereotype.Service
import ru.prohor.universe.venator.webhook.model.WebhookPayload

@Service
class WebhookActionImpl : WebhookAction {
    override fun accept(payload: WebhookPayload) {
        println(payload)
    }
}
