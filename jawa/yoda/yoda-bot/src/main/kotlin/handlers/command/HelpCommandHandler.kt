package ru.prohor.universe.yoda.bot.handlers.command

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import ru.prohor.universe.jocasta.core.collections.common.Opt
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler
import ru.prohor.universe.yoda.bot.Text.Commands
import ru.prohor.universe.yoda.bot.Text.CommandDescriptions

@Service
class HelpCommandHandler(commandHandlers: List<CommandHandler>) : CommandHandler {
    private val commandsListMessage by lazy {
        createMessage(commandHandlers)
    }

    override fun command(): String {
        return Commands.HELP
    }

    override fun description(): Opt<String> {
        return Opt.of(CommandDescriptions.help())
    }

    override fun handle(message: Message, feedbackExecutor: FeedbackExecutor) {
        feedbackExecutor.sendMessage(
            message.chatId,
            commandsListMessage
        )
    }

    private fun createMessage(handlers: List<CommandHandler>): String {
        return handlers
            .plus(this)
            .filter { cmd -> cmd.description().isPresent }
            .joinToString(separator = "\n") { cmd ->
                "${cmd.command()} - ${cmd.description().get()}"
            }
    }
}
