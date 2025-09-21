package ru.prohor.universe.jocasta.tgbots;

import ru.prohor.universe.jocasta.core.functional.DiFunction;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusStorageService;
import ru.prohor.universe.jocasta.tgbots.support.callback.CallbackSupport;
import ru.prohor.universe.jocasta.tgbots.support.command.CommandSupport;
import ru.prohor.universe.jocasta.tgbots.support.command.CommandSupportImpl;
import ru.prohor.universe.jocasta.tgbots.support.status.StatusSupport;
import ru.prohor.universe.jocasta.tgbots.support.status.StatusSupportImpl;

import java.util.List;

public final class TgBotSettings {
    final String token;
    final String username;
    final CommandSupport commandSupport;
    final CallbackSupport callbackSupport;
    final StatusSupport statusSupport;

    private TgBotSettings(
            String token,
            String username,
            CommandSupport commandSupport,
            CallbackSupport callbackSupport,
            StatusSupport statusSupport
    ) {
        this.token = token;
        this.username = username;
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
        private CommandSupport commandSupport;
        private CallbackSupport callbackSupport;
        private StatusSupport statusSupport;

        private Builder(String token, String username) {
            this.token = token;
            this.username = username;
        }

        /**
         * @param commandHandlers       command handlers
         * @param unknownCommandHandler handler for unknown command, accepts command and feedback executor,
         *                              returns flag indicating whether to continue update processing
         * @return this builder
         */
        public Builder withCommandSupport(
                List<CommandHandler> commandHandlers,
                DiFunction<String, FeedbackExecutor, Boolean> unknownCommandHandler
        ) {
            commandSupport = new CommandSupportImpl(username, commandHandlers, unknownCommandHandler);
            return this;
        }

        public Builder withCallbackSupport() {
            callbackSupport = null; // TODO
            return this;
        }

        /**
         * @param statusesCacheSize    size of cache with statuses from storage
         * @param statusStorageService implementation of statuses storage
         * @param statusHandlers       status handlers
         * @param <SK>                 status key type
         * @param <SV>                 status value type
         * @return this builder
         */
        public <SK, SV> Builder withStatusSupport(
                int statusesCacheSize,
                StatusStorageService<SK, SV> statusStorageService,
                List<StatusHandler<SK, SV>> statusHandlers
        ) {
            statusSupport = new StatusSupportImpl<>(statusesCacheSize, statusStorageService, statusHandlers);
            return this;
        }

        public TgBotSettings build() {
            return new TgBotSettings(
                    token,
                    username,
                    commandSupport == null ? CommandSupport.getUnsupported() : commandSupport,
                    callbackSupport == null ? CallbackSupport.getUnsupported() : callbackSupport,
                    statusSupport == null ? StatusSupport.getUnsupported() : statusSupport
            );
        }
    }
}
