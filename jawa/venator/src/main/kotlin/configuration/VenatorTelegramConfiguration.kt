package ru.prohor.universe.venator.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.prohor.universe.jocasta.tgbots.BotAuth
import ru.prohor.universe.jocasta.tgbots.RegisterBot
import ru.prohor.universe.venator.telegram.VenatorBot

@Configuration
class VenatorTelegramConfiguration {
    @Bean
    fun botAuth(
        @Value($$"${universe.venator.bot.token}") token: String,
        @Value($$"${universe.venator.bot.username}") username: String
    ): BotAuth = BotAuth(username, token)

    @Bean
    fun venatorBot(auth: BotAuth): VenatorBot {
        return RegisterBot.register(VenatorBot(auth))
    }
}
