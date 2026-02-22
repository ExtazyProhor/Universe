package ru.prohor.universe.yoda.bot.handlers.command

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import ru.prohor.universe.jocasta.core.collections.common.Opt
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler
import ru.prohor.universe.yoda.bot.Text.Commands
import ru.prohor.universe.yoda.bot.Text.CommandDescriptions
import ru.prohor.universe.yoda.bot.Text.CommandReplies

@Service
class StartCommandHandler : CommandHandler {
    override fun command(): String {
        return Commands.START
    }

    override fun description(): Opt<String> {
        return Opt.of(CommandDescriptions.start())
    }

    override fun handle(message: Message, feedbackExecutor: FeedbackExecutor) {
        feedbackExecutor.sendMessage(message.chatId, CommandReplies.start())
    }
}
