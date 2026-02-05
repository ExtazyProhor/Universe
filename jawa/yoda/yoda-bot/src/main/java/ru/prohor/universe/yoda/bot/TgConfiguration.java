package ru.prohor.universe.yoda.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.RegisterBot;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.jocasta.tgbots.api.comand.NonCommandMessageHandler;

import java.util.List;

@Configuration
public class TgConfiguration {
    @Bean
    public BotSettings botSettings(
            @Value("${universe.yoda.bot.token}") String token,
            @Value("${universe.yoda.bot.username}") String username,
            List<CommandHandler> commandHandlers,
            UnknownActionKeyHandler<Message, String> unknownCommandHandler,
            NonCommandMessageHandler nonCommandMessageHandler
    ) {
        return BotSettings.builder(token, username)
                .withCommandSupport(commandHandlers, unknownCommandHandler, nonCommandMessageHandler)
                .build();
    }

    @Bean
    public YodaBot yodaBot(BotSettings settings) {
        return RegisterBot.register(new YodaBot(settings));
    }
}
