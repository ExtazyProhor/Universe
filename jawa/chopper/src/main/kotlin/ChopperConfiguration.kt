package ru.prohor.universe.chopper

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.prohor.universe.jocasta.tgbots.BotAuth

@Configuration
class ChopperConfiguration {
    @Bean
    fun chopperBot(
        @Value($$"${universe.chopper.bot-username}") username: String,
        @Value($$"${universe.chopper.bot-token}") token: String
    ): ChopperBot {
        return ChopperBot(BotAuth(username, token))
    }
}
