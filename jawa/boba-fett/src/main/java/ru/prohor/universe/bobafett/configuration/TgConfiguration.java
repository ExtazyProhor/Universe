package ru.prohor.universe.bobafett.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.bobafett.BobaFettBot;
import ru.prohor.universe.bobafett.command.StartCommand;
import ru.prohor.universe.bobafett.data.MongoStatusStorage;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.bobafett.service.ObjectsEncoder;
import ru.prohor.universe.jocasta.tgbots.BotSettings;
import ru.prohor.universe.jocasta.tgbots.RegisterBot;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.ValuedCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusHandler;

import java.util.List;

@Configuration
public class TgConfiguration {
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
            UnknownInputHandlers unknownInputHandlers,
            ObjectMapper objectMapper
    ) {
        return BotSettings.builder(token, username)
                .withCommandSupport(
                        commands,
                        unknownInputHandlers::handleUnknownCommand
                )
                .withCallbackSupport(
                        callbacks,
                        valuedCallbacks,
                        jsonCallbacks,
                        objectMapper,
                        unknownInputHandlers::handleUnknownCallback
                )
                .withStatusSupport(
                        mongoStatusStorage,
                        statuses,
                        valuedStatuses,
                        unknownInputHandlers::handleUnknownStatus
                )
                .build();
    }

    @Bean
    public BobaFettBot bobaFettBot(
            BobaFettUserService bobaFettUserService,
            ObjectsEncoder objectsEncoder,
            BotSettings settings,
            StartCommand startCommand
    ) {
        return RegisterBot.register(new BobaFettBot(bobaFettUserService, objectsEncoder, settings, startCommand));
    }
}
