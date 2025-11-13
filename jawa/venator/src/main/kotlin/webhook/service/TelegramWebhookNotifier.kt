package ru.prohor.universe.venator.webhook.service

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.prohor.universe.venator.telegram.VenatorBot
import ru.prohor.universe.venator.webhook.WebhookNotifier
import ru.prohor.universe.venator.webhook.model.WebhookPayload

@Service
class TelegramWebhookNotifier(
    @param:Value($$"${universe.venator.notifiable-chat-id}")
    private val notifiableChatId: Long,
    private val bot: VenatorBot
) : WebhookNotifier {
    private val formatter = DateTimeFormat.forPattern("HH:mm:ss dd-MM-yyyy")
    private val zone = DateTimeZone.forID("Europe/Moscow")

    override fun failure(message: String) {
        bot.feedbackExecutor.sendMessage(message.toFailureMessage())
    }

    override fun info(message: String) {
        bot.feedbackExecutor.sendMessage(message.toInfoMessage())
    }

    override fun success(payload: WebhookPayload) {
        bot.feedbackExecutor.sendMessage(payload.toMessage())
    }

    private fun String.toFailureMessage(): SendMessage {
        val message = "\u274c failures at webhook controller:\n*$this*"
        return SendMessage.builder()
            .chatId(notifiableChatId)
            .text(message)
            .parseMode("Markdown")
            .build()
    }

    private fun String.toInfoMessage(): SendMessage {
        return SendMessage.builder()
            .chatId(notifiableChatId)
            .text(this)
            .build()
    }

    private fun WebhookPayload.toMessage(): SendMessage {
        val login = sender.login
        val commit = headCommit.message
        val datetime = formatter.print(DateTime(repository.pushedAt * 1000, zone))
        val message = "\u2705 $login pushed new changes to Universe at\n_${datetime}_.\nLast commit is:\n\n*$commit*"
        return SendMessage.builder()
            .chatId(notifiableChatId)
            .text(message)
            .parseMode("Markdown")
            .build()
    }
}
