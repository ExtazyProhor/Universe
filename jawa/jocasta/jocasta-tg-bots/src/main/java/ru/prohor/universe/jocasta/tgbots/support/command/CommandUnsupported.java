package ru.prohor.universe.jocasta.tgbots.support.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public class CommandUnsupported implements CommandSupport {
    @Override
    public boolean handleMessage(Message message, FeedbackExecutor feedbackExecutor) {
        return true;
    }
}
