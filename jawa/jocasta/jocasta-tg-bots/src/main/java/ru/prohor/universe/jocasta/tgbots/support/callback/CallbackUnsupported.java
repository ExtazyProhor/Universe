package ru.prohor.universe.jocasta.tgbots.support.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public class CallbackUnsupported implements CallbackSupport {
    @Override
    public boolean handleCallback(CallbackQuery callback, FeedbackExecutor feedbackExecutor) {
        return true;
    }
}
