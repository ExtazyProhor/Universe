package ru.prohor.universe.bobafett.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.BobaFettBot;
import ru.prohor.universe.bobafett.data.MongoStatusStorage;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.RegisterBot;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;
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
        UNKNOWN_COMMAND = ((message, command, feedbackExecutor) -> {
            // TODO log trace unknown command from chat ...
            return false;
        });

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
            List<CommandHandler> commandHandlers,
            List<ValuedCallbackHandler> valuedCallbackHandlers,
            List<CallbackHandler> callbackHandlers,
            List<ValuedStatusHandler<String, String>> valuedStatusHandlers,
            List<StatusHandler<String>> statusHandlers,
            MongoStatusStorage mongoStatusStorage
    ) {
        return BotSettings.builder(token, username)
                .withCommandSupport(commandHandlers, UNKNOWN_COMMAND)
                .withCallbackSupport(callbackHandlers, valuedCallbackHandlers, UNKNOWN_CALLBACK)
                .withStatusSupport(mongoStatusStorage, statusHandlers, valuedStatusHandlers, UNKNOWN_STATUS)
                .build();
    }

    @Bean
    public BobaFettBot bobaFettBot(BotSettings settings) {
        return RegisterBot.register(new BobaFettBot(settings));
    }
}
