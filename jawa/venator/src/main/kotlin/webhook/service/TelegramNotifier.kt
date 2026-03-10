package ru.prohor.universe.venator.webhook.service

import org.springframework.beans.factory.annotation.Value
import ru.prohor.universe.chopper.client.ChopperClient
import ru.prohor.universe.venator.shared.Notifier

class TelegramNotifier(
    @param:Value($$"${universe.venator.notifiable-chat-id}")
    private val notifiableChatId: Long,
    private val chopperClient: ChopperClient
) : Notifier {
    override fun failure(message: String) {
        send("\u274c $message")
    }

    override fun failure(message: String, fileContent: String, fileName: String) {
        chopperClient.sendFile(
            content = fileContent,
            chatId = notifiableChatId,
            fileName = fileName,
            caption = "\u274c $message"
        )
    }

    override fun info(message: String) {
        send(message)
    }

    override fun success(message: String) {
        send("\u2705 $message")
    }

    private fun send(message: String) {
        chopperClient.sendMessage(
            text = message,
            chatId = notifiableChatId,
            markdown = true
        )
    }
}
