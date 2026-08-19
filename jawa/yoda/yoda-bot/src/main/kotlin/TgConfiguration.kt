package ru.prohor.universe.yoda.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.prohor.universe.jocasta.tgbots.BotSettings
import ru.prohor.universe.jocasta.tgbots.TelegramBotsConfiguration
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler
import ru.prohor.universe.jocasta.tgbots.api.comand.NonCommandMessageHandler

@Import(TelegramBotsConfiguration::class)
@Configuration
class TgConfiguration {
    @Bean
    fun botSettings(
        @Value($$"${universe.yoda.bot.token}") token: String,
        @Value($$"${universe.yoda.bot.username}") username: String,
        commandHandlers: List<CommandHandler>,
        unknownCommandHandler: UnknownActionKeyHandler<Message, String>,
        nonCommandMessageHandler: NonCommandMessageHandler
    ): BotSettings {
        return BotSettings.builder(token, username)
            .withCommandSupport(commandHandlers, unknownCommandHandler, nonCommandMessageHandler)
            .build()
    }

    @Bean
    fun yodaBot(settings: BotSettings): YodaBot {
        return YodaBot(settings)
    }
}
