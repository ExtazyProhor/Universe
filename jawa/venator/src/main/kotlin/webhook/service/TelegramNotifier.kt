package ru.prohor.universe.venator.webhook.service

import org.springframework.beans.factory.annotation.Value
import ru.prohor.universe.chopper.client.ChopperClient
import ru.prohor.universe.chopper.client.MarkdownV2
import ru.prohor.universe.venator.shared.Notifier

class TelegramNotifier(
    @param:Value($$"${universe.venator.notifiable-chat-id}")
    private val notifiableChatId: Long,
    private val chopperClient: ChopperClient
) : Notifier {
    override fun failure(message: MarkdownV2) {
        send("\u274c $message")
    }

    override fun failure(message: MarkdownV2, fileContent: String, fileName: String) {
        chopperClient.sendFile(
            content = fileContent,
            chatId = notifiableChatId,
            fileName = fileName,
            caption = "\u274c $message"
        )
    }

    override fun info(message: MarkdownV2) {
        send(message.toMarkdown())
    }

    override fun success(message: MarkdownV2) {
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
