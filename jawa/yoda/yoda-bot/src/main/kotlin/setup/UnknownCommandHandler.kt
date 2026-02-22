package ru.prohor.universe.yoda.bot.setup

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler
import ru.prohor.universe.yoda.bot.Text.CommandReplies

@Service
class UnknownCommandHandler : UnknownActionKeyHandler<Message, String> {
    override fun handleUnknownActionKey(
        message: Message,
        key: String,
        feedbackExecutor: FeedbackExecutor
    ) {
        val chat = message.chat
        if (chat.isUserChat) {
            feedbackExecutor.sendMessage(chat.id, CommandReplies.unknown())
        }
    }
}
