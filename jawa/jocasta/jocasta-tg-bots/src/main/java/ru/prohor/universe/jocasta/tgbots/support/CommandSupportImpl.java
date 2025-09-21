package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.core.functional.DiFunction;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

import java.util.List;

public class CommandSupportImpl extends FeatureSupportImpl<Message, String, CommandHandler> {
    private final String botUsername;

    public CommandSupportImpl(
            String botUsername,
            List<CommandHandler> handlers,
            DiFunction<String, FeedbackExecutor, Boolean> unknownCommandHandler
    ) {
        super(handlers, unknownCommandHandler);
        this.botUsername = botUsername;
    }

    @Override
    public boolean handle(Message message, FeedbackExecutor feedbackExecutor) {
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
        return useHandler(text, handler -> handler.handle(feedbackExecutor), feedbackExecutor);
    }
}
