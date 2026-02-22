package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.jocasta.tgbots.api.comand.NonCommandMessageHandler;

import java.util.List;

public class CommandSupportImpl extends FeatureSupportImpl<Message, String, CommandHandler> {
    private final String botUsername;
    private final NonCommandMessageHandler nonCommandMessageHandler;

    public CommandSupportImpl(
            String botUsername,
            List<CommandHandler> handlers,
            UnknownActionKeyHandler<Message, String> unknownCommandHandler,
            NonCommandMessageHandler nonCommandMessageHandler
    ) {
        super(handlers, unknownCommandHandler);
        this.botUsername = botUsername;
        this.nonCommandMessageHandler = nonCommandMessageHandler;
    }

    @Override
    public void handle(Message message, FeedbackExecutor feedbackExecutor) {
        String text = message.getText().trim();
        if (!text.startsWith("/")) {
            nonCommandMessageHandler.onNonCommandMessage(message, feedbackExecutor);
            return;
        }
        int atIndex = text.indexOf('@');
        if (atIndex != -1) {
            String username = text.substring(atIndex + 1);
            if (!username.equals(botUsername))
                return;
            text = text.substring(0, atIndex);
        }
        // TODO log trace text (it is not private, ony started with slash)
        useHandler(
                message,
                text,
                handler -> handler.handle(message, feedbackExecutor),
                feedbackExecutor
        );
    }
}
