package ru.prohor.universe.jocasta.tgbots.api.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.string.StringExtensions;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public abstract class JsonCallbackHandler<T> implements ActionHandler<String> {
    private static final int MAX_CALLBACK_LENGTH = 64;

    /**
     * The type of class that is mapped to JSON
     */
    private final String prefix;
    private final Class<T> type;
    private final ObjectMapper objectMapper;

    public JsonCallbackHandler(String prefix, Class<T> type, ObjectMapper objectMapper) {
        this.prefix = prefix;
        this.type = type;
        this.objectMapper = objectMapper;
    }

    /**
     * @return callback prefix.
     * Valued callback data must have format <code>prefix.payload</code>.
     * Note, that <code>prefix</code> and <code>payload</code> mustn't contain "." symbols.
     */
    public final String prefix() {
        return prefix;
    }

    @Override
    public final String key() {
        return prefix();
    }

    /**
     * @param payload          callback payload
     * @param message          telegram api message
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    protected abstract boolean handle(T payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor);

    public final String makeCallback(T payload) {
        String payloadString = Sneaky.execute(() -> objectMapper.writeValueAsString(payload));
        if (payloadString.contains("."))
            throw new IllegalArgumentException("Callback payload must not contains dots ('.')");
        String callback = prefix + "." + payloadString;
        int len = StringExtensions.utf8Length(callback);
        if (len <= MAX_CALLBACK_LENGTH)
            return callback;
        throw new IllegalArgumentException(
                "callback exceeds the maximum size of " + MAX_CALLBACK_LENGTH + " bytes and is " + len + " bytes"
        );
    }

    public final boolean handle(
            String payloadString,
            ObjectMapper objectMapper,
            MaybeInaccessibleMessage message,
            FeedbackExecutor feedbackExecutor
    ) {
        return Sneaky.execute(() -> {
            T payload = objectMapper.readValue(payloadString, type);
            return handle(payload, message, feedbackExecutor);
        });
    }
}
