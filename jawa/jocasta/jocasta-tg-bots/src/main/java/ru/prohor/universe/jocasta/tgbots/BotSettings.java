package ru.prohor.universe.jocasta.tgbots;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.ValuedCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusStorage;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusStorage;
import ru.prohor.universe.jocasta.tgbots.support.CallbackSupportImpl;
import ru.prohor.universe.jocasta.tgbots.support.FeatureSupport;
import ru.prohor.universe.jocasta.tgbots.support.FeatureUnsupported;
import ru.prohor.universe.jocasta.tgbots.support.CommandSupportImpl;
import ru.prohor.universe.jocasta.tgbots.support.JsonCallbackSupportImpl;
import ru.prohor.universe.jocasta.tgbots.support.StatusSupportImpl;
import ru.prohor.universe.jocasta.tgbots.support.ValuedCallbackSupportImpl;
import ru.prohor.universe.jocasta.tgbots.support.ValuedStatusSupportImpl;

import java.util.List;

public final class BotSettings {
    final BotAuth auth;

    final FeatureSupport<Message> commandSupport;
    final FeatureSupport<CallbackQuery> callbackSupport;
    final FeatureSupport<Update> statusSupport;

    private BotSettings(
            String token,
            String username,
            FeatureSupport<Message> commandSupport,
            FeatureSupport<CallbackQuery> callbackSupport,
            FeatureSupport<Update> statusSupport
    ) {
        this.auth = new BotAuth(username, token);
        this.commandSupport = commandSupport;
        this.callbackSupport = callbackSupport;
        this.statusSupport = statusSupport;
    }

    public static Builder builder(String token, String username) {
        return new Builder(token, username);
    }

    public static final class Builder {
        private final String token;
        private final String username;

        private FeatureSupport<Message> commandSupport;
        private FeatureSupport<CallbackQuery> callbackSupport;
        private FeatureSupport<Update> statusSupport;

        private Builder(String token, String username) {
            this.token = token;
            this.username = username;
        }

        /**
         * @param commandHandlers       command handlers
         * @param unknownCommandHandler handler for unknown command, accepts unknown command and feedback executor,
         *                              returns flag indicating whether to continue update processing
         * @return this builder
         */
        public Builder withCommandSupport(
                List<CommandHandler> commandHandlers,
                TriFunction<Message, String, FeedbackExecutor, Boolean> unknownCommandHandler
        ) {
            commandSupport = new CommandSupportImpl(username, commandHandlers, unknownCommandHandler);
            return this;
        }

        /**
         * Support normal, valued and json callbacks
         *
         * @param handlers               callback handlers
         * @param valuedHandlers         valued callback handlers
         * @param jsonHandlers           json callback handlers
         * @param objectMapper           json mapper
         * @param unknownCallbackHandler handler for unknown callback, accepts unknown callback and feedback executor,
         *                               returns flag indicating whether to continue update processing
         * @return this builder
         */
        public Builder withCallbackSupport(
                List<CallbackHandler> handlers,
                List<ValuedCallbackHandler> valuedHandlers,
                List<JsonCallbackHandler<?>> jsonHandlers,
                ObjectMapper objectMapper,
                TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler
        ) {
            callbackSupport = new JsonCallbackSupportImpl(
                    handlers,
                    valuedHandlers,
                    jsonHandlers,
                    objectMapper,
                    unknownCallbackHandler
            );
            return this;
        }

        /**
         * Support both normal and valued callbacks
         *
         * @param handlers               callback handlers
         * @param valuedHandlers         valued callback handlers
         * @param unknownCallbackHandler handler for unknown callback, accepts unknown callback and feedback executor,
         *                               returns flag indicating whether to continue update processing
         * @return this builder
         */
        public Builder withCallbackSupport(
                List<CallbackHandler> handlers,
                List<ValuedCallbackHandler> valuedHandlers,
                TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler
        ) {
            callbackSupport = new ValuedCallbackSupportImpl(handlers, valuedHandlers, unknownCallbackHandler);
            return this;
        }

        /**
         * Only support callbacks without values
         *
         * @param handlers               callback handlers
         * @param unknownCallbackHandler handler for unknown callback, accepts unknown callback and feedback executor,
         *                               returns flag indicating whether to continue update processing
         * @return this builder
         */
        public Builder withCallbackSupport(
                List<CallbackHandler> handlers,
                TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler
        ) {
            callbackSupport = new CallbackSupportImpl(handlers, unknownCallbackHandler);
            return this;
        }

        /**
         * Support both normal and valued statuses
         *
         * @param valuedStatusStorage  implementation of valued statuses storage
         * @param statusHandlers       status handlers
         * @param valuedStatusHandlers valued status handlers
         * @param unknownStatusHandler handler for unknown status, accepts unknown status key and feedback executor,
         *                             returns flag indicating whether to continue update processing
         * @param <K>                  status key type
         * @param <V>                  status value type
         * @return this builder
         */
        public <K, V> Builder withStatusSupport(
                ValuedStatusStorage<K, V> valuedStatusStorage,
                List<StatusHandler<K>> statusHandlers,
                List<ValuedStatusHandler<K, V>> valuedStatusHandlers,
                TriFunction<Update, K, FeedbackExecutor, Boolean> unknownStatusHandler
        ) {
            statusSupport = new ValuedStatusSupportImpl<>(
                    statusHandlers,
                    valuedStatusHandlers,
                    unknownStatusHandler,
                    valuedStatusStorage
            );
            return this;
        }

        /**
         * Only support statuses without values
         *
         * @param statusStorage        implementation of statuses storage
         * @param statusHandlers       status handlers
         * @param unknownStatusHandler handler for unknown status, accepts unknown status key and feedback executor,
         *                             returns flag indicating whether to continue update processing
         * @param <K>                  status key type
         * @return this builder
         */
        public <K> Builder withStatusSupport(
                StatusStorage<K> statusStorage,
                List<StatusHandler<K>> statusHandlers,
                TriFunction<Update, K, FeedbackExecutor, Boolean> unknownStatusHandler
        ) {
            statusSupport = new StatusSupportImpl<>(statusStorage, statusHandlers, unknownStatusHandler);
            return this;
        }

        public BotSettings build() {
            return new BotSettings(
                    token,
                    username,
                    commandSupport == null ? new FeatureUnsupported<>() : commandSupport,
                    callbackSupport == null ? new FeatureUnsupported<>() : callbackSupport,
                    statusSupport == null ? new FeatureUnsupported<>() : statusSupport
            );
        }
    }
}
