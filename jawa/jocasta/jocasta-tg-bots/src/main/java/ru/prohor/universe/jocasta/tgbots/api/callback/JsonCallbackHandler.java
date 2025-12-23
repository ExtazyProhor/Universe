package ru.prohor.universe.jocasta.tgbots.api.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface JsonCallbackHandler<T> extends ActionHandler<String> {
    /**
     * @return callback prefix.
     * Valued callback data must have format <code>prefix.payload</code>.
     * Note, that <code>prefix</code> and <code>payload</code> mustn't contain "." symbols.
     */
    String prefix();

    /**
     * @return the type of class that is mapped to JSON
     */
    Class<T> type();

    @Override
    default String key() {
        return prefix();
    }

    /**
     * @param payload          callback payload
     * @param message          telegram api message
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(T payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor);

    default String makeCallback(String payload) {
        return prefix() + "." + payload;
    }

    default boolean handle(
            String payloadString,
            ObjectMapper objectMapper,
            MaybeInaccessibleMessage message,
            FeedbackExecutor feedbackExecutor
    ) {
        return Sneaky.execute(() -> {
            T payload = objectMapper.readValue(payloadString, type());
            return handle(payload, message, feedbackExecutor);
        });
    }
}
