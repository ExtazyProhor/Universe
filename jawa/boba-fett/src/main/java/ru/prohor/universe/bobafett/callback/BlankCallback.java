package ru.prohor.universe.bobafett.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class BlankCallback implements CallbackHandler {
    @Override
    public String callback() {
        return Callbacks.BLANK;
    }

    @Override
    public void handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {}
}
