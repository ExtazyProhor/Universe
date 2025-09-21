package ru.prohor.universe.jocasta.tgbots.support.status;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public class StatusUnsupported implements StatusSupport {
    @Override
    public boolean handleUpdate(Update update, FeedbackExecutor feedbackExecutor) {
        return true;
    }
}
