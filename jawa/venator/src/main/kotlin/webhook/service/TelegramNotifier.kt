package ru.prohor.universe.venator.webhook.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.prohor.universe.venator.shared.Notifier
import ru.prohor.universe.venator.shared.VenatorBot

@Service
class TelegramNotifier(
    @param:Value($$"${universe.venator.notifiable-chat-id}")
    private val notifiableChatId: Long,
    private val bot: VenatorBot
) : Notifier {
    override fun failure(message: String) {
        send("\u274c $message")
    }

    override fun info(message: String) {
        send(message)
    }

    override fun success(message: String) {
        send("\u2705 $message")
    }

    private fun send(message: String) {
        bot.feedbackExecutor.sendMessage(
            SendMessage.builder()
                .chatId(notifiableChatId)
                .text(message)
                .parseMode("Markdown")
                .build()
        )
    }
}
