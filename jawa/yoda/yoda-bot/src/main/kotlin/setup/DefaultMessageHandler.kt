package ru.prohor.universe.yoda.bot.setup

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.jocasta.tgbots.api.comand.NonCommandMessageHandler
import ru.prohor.universe.yoda.bot.Text.CommandReplies

@Service
class DefaultMessageHandler : NonCommandMessageHandler {
    override fun onNonCommandMessage(message: Message, feedbackExecutor: FeedbackExecutor) {
        if (message.chat.isUserChat) {
            feedbackExecutor.sendMessage(
                message.chatId,
                CommandReplies.nonCommand(message.text)
            )
        }
    }
}
