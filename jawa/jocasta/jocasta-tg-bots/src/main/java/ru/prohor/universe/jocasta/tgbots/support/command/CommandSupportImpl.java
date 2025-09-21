package ru.prohor.universe.jocasta.tgbots.support.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.core.functional.DiFunction;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandSupportImpl implements CommandSupport {
    private final String botUsername;
    private final Map<String, CommandHandler> commandHandlers;
    private final DiFunction<String, FeedbackExecutor, Boolean> unknownCommandHandler;

    public CommandSupportImpl(
            String botUsername,
            List<CommandHandler> commandHandlers,
            DiFunction<String, FeedbackExecutor, Boolean> unknownCommandHandler
    ) {
        this.botUsername = botUsername;
        this.commandHandlers = commandHandlers.stream().collect(Collectors.toMap(
                CommandHandler::command,
                MonoFunction.identity()
        ));
        this.unknownCommandHandler = unknownCommandHandler;
    }

    @Override
    public boolean handleMessage(Message message, FeedbackExecutor feedbackExecutor) {
        String text = message.getText().trim();
        if (!text.startsWith("/"))
            return true;
        int atIndex = text.indexOf('@');
        if (atIndex != -1) {
            String username = text.substring(atIndex + 1);
            if (!username.equals(botUsername))
                return false;
            text = text.substring(0, atIndex);
        }
        // log trace text
        if (commandHandlers.containsKey(text)) {
            CommandHandler handler = commandHandlers.get(text);
            return handler.handle(feedbackExecutor);
        }
        return unknownCommandHandler.apply(text, feedbackExecutor);
    }
}
