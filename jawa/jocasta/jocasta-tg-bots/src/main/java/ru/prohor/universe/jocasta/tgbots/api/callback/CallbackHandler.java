package ru.prohor.universe.jocasta.tgbots.api.callback;

import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface CallbackHandler extends ActionHandler<String> {
    /**
     * @return callback string.
     * Note, that callback string mustn't contain "." symbols.
     */
    String callback();

    @Override
    default String key() {
        return callback();
    }

    /**
     * @param message          telegram api message
     * @param feedbackExecutor interface for sending feedback to users
     */
    void handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor);
}
