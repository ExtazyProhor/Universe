package ru.prohor.universe.jocasta.tgbots.api.callback;

import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface CallbackHandler extends ActionHandler<String> {
    /**
     * @return callback prefix.
     * Callback data must have format <code>prefix.payload</code> or <code>prefix</code>.
     * Note, that <code>prefix</code> and <code>payload</code> mustn't contain "." symbols.
     */
    String prefix();

    @Override
    default String key() {
        return prefix();
    }

    /**
     * @param callbackPayload  optional callback payload
     * @param message          telegram api message
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(Opt<String> callbackPayload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor);
}
