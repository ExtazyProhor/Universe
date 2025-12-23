package ru.prohor.universe.jocasta.tgbots.api.callback;

import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface ValuedCallbackHandler extends ActionHandler<String> {
    /**
     * @return callback prefix.
     * Valued callback data must have format <code>prefix.payload</code>.
     * Note, that <code>prefix</code> and <code>payload</code> mustn't contain "." symbols.
     */
    String prefix();

    @Override
    default String key() {
        return prefix();
    }

    /**
     * @param callbackPayload  callback payload
     * @param message          telegram api message
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(String callbackPayload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor);

    default String makeCallback(String payload) {
        if (payload.contains("."))
            throw new IllegalArgumentException("Callback payload must not contains dots ('.')");
        return prefix() + "." + payload;
    }
}
