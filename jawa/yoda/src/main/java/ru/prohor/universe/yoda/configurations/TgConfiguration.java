package ru.prohor.universe.yoda.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

import java.util.List;

@Configuration
public class TgConfiguration {
    @Bean
    public TriFunction<Message, String, FeedbackExecutor, Boolean> unknownCommandHandler() {
        return (message, command, feedbackExecutor) -> {
            Chat chat = message.getChat();
            if (chat.isUserChat()) {
                feedbackExecutor.sendMessage(
                        SendMessage.builder()
                                .chatId(chat.getId()) // slf4j.helpers.MessageFormatter
                                .text("Неизвестная команда. Список доступных команд - /help")
                                .build()
                );
            }
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
}
