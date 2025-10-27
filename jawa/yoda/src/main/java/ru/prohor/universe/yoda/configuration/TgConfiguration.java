package ru.prohor.universe.yoda.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.RegisterBot;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.yoda.bot.Text;
import ru.prohor.universe.yoda.bot.YodaBot;
import ru.prohor.universe.yoda.log.FileLogger;

import java.util.List;

@Configuration
public class TgConfiguration {
    @Bean
    public TriFunction<Message, String, FeedbackExecutor, Boolean> unknownCommandHandler() {
        return (message, command, feedbackExecutor) -> {
            Chat chat = message.getChat();
            if (chat.isUserChat())
                feedbackExecutor.sendMessage(chat.getId(), Text.CommandReplies.unknown());
            return false;
        };
    }

    @Bean
    public BotSettings botSettings(
            @Value("${universe.yoda.bot.token}") String token,
            @Value("${universe.yoda.bot.username}") String username,
            List<CommandHandler> commandHandlers,
            TriFunction<Message, String, FeedbackExecutor, Boolean> unknownCommandHandler
    ) {
        return BotSettings.builder(token, username)
                .withCommandSupport(commandHandlers, unknownCommandHandler)
                .build();
    }

    @Bean
    public YodaBot yodaBot(BotSettings settings, FileLogger logger) {
        return RegisterBot.register(new YodaBot(settings, logger));
    }
}
