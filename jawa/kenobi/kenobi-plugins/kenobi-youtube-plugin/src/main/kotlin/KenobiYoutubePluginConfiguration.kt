package ru.prohor.universe.kenobi.plugin.youtube

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import ru.prohor.universe.jocasta.jackson.morphia.MongoFileRepository
import ru.prohor.universe.jocasta.morphia.MongoRepository
import ru.prohor.universe.jocasta.tgbots.BotAuth
import ru.prohor.universe.jocasta.tgbots.RegisterBot
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.kenobi.plugin.youtube.model.Subscriber
import ru.prohor.universe.kenobi.plugin.youtube.tg.KenobiBot

@Configuration
@ComponentScan
@PropertySource("classpath:kenobi-youtube-plugin.properties")
class KenobiYoutubePluginConfiguration {
    @Bean
    fun subscribersCollection(
        @Value($$"${universe.kenobi.plugins.youtube.collection-file.subscribers}") subscribersFile: String,
        objectMapper: ObjectMapper
    ): MongoRepository<Subscriber> {
        return MongoFileRepository(
            Subscriber::id,
            Subscriber::class.java,
            subscribersFile,
            objectMapper
        )
    }

    @Bean
    fun kenobiBot(
        @Value($$"${universe.kenobi.plugins.youtube.tg-bot.username}") username: String,
        @Value($$"${universe.kenobi.plugins.youtube.tg-bot.token}") token: String
    ): KenobiBot {
        val auth = BotAuth(username, token)
        val bot = KenobiBot(auth)
        RegisterBot.register(bot)
        return bot
    }

    @Bean
    fun feedbackExecutor(kenobiBot: KenobiBot): FeedbackExecutor {
        return kenobiBot.feedbackExecutor
    }
}
