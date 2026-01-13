package ru.prohor.universe.bobafett.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.BobaFettBot;
import ru.prohor.universe.bobafett.data.MongoStatusStorage;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.RegisterBot;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.ValuedCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusHandler;

import java.util.List;

@Configuration
public class TgConfiguration {
    private static final TriFunction<Message, String, FeedbackExecutor, Boolean> UNKNOWN_COMMAND;
    private static final TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> UNKNOWN_CALLBACK;
    private static final TriFunction<Update, String, FeedbackExecutor, Boolean> UNKNOWN_STATUS;

    static {
        UNKNOWN_COMMAND = (message, command, feedbackExecutor) -> {
            feedbackExecutor.sendMessage(
                    message.getChatId(),
                    "Неизвестная команда. Посмотреть список доступных команд - /commands"
            );
            return false;
        };

        UNKNOWN_CALLBACK = ((callback, s, feedbackExecutor) -> {
            // TODO log error unknown callback from chat ...
            return false;
        });

        UNKNOWN_STATUS = ((update, statusKey, feedbackExecutor) -> {
            // TODO log error unknown status from chat ...
            return false;
        });
    }

    @Bean
    public BotSettings botSettings(
            @Value("${universe.boba-fett.bot-token}") String token,
            @Value("${universe.boba-fett.bot-username}") String username,
            List<CommandHandler> commands,
            List<ValuedCallbackHandler> valuedCallbacks,
            List<JsonCallbackHandler<?>> jsonCallbacks,
            List<CallbackHandler> callbacks,
            List<ValuedStatusHandler<String, String>> valuedStatuses,
            List<StatusHandler<String>> statuses,
            MongoStatusStorage mongoStatusStorage,
            ObjectMapper objectMapper
    ) {
        return BotSettings.builder(token, username)
                .withCommandSupport(commands, UNKNOWN_COMMAND)
                .withCallbackSupport(callbacks, valuedCallbacks, jsonCallbacks, objectMapper, UNKNOWN_CALLBACK)
                .withStatusSupport(mongoStatusStorage, statuses, valuedStatuses, UNKNOWN_STATUS)
                .build();
    }

    @Bean
    public BobaFettBot bobaFettBot(
            BobaFettUserService bobaFettUserService,
            ObjectMapper objectMapper,
            BotSettings settings
    ) {
        return RegisterBot.register(new BobaFettBot(bobaFettUserService, objectMapper, settings));
    }
}
