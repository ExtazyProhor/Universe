package ru.prohor.universe.kenobi.app

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.prohor.universe.jocasta.tgbots.BotAuth
import ru.prohor.universe.jocasta.tgbots.TelegramBotsConfiguration
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.kenobi.plugin.youtube.tg.KenobiBot

@Import(TelegramBotsConfiguration::class)
@Configuration
class KenobiTelegramConfiguration {
    @Bean
    fun kenobiBot(
        @Value($$"${universe.kenobi.app.tg-bot.username}") username: String,
        @Value($$"${universe.kenobi.app.tg-bot.token}") token: String
    ): KenobiBot {
        return KenobiBot(BotAuth(username, token))
    }

    @Bean
    fun feedbackExecutor(kenobiBot: KenobiBot): FeedbackExecutor {
        return kenobiBot.feedbackExecutor
    }
}
