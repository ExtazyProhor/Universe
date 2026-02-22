package ru.prohor.universe.jocasta.tgbots.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.ValuedCallbackHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonCallbackSupportImpl extends AbstractValuedCallbackSupportImpl {
    private final Map<String, ValuedCallbackHandler> valuedHandlers;
    private final Map<String, JsonCallbackHandler<?>> jsonHandlers;
    private final ObjectMapper objectMapper;

    public JsonCallbackSupportImpl(
            List<CallbackHandler> handlers,
            List<ValuedCallbackHandler> valuedHandlers,
            List<JsonCallbackHandler<?>> jsonHandlers,
            ObjectMapper objectMapper,
            UnknownActionKeyHandler<CallbackQuery, String> unknownCallbackPrefixHandler
    ) {
        super(handlers, unknownCallbackPrefixHandler);
        Map<String, Class<?>> duplicates = new HashMap<>();
        this.valuedHandlers = new HashMap<>();
        this.jsonHandlers = new HashMap<>();
        initializeMap(duplicates, valuedHandlers, this.valuedHandlers);
        initializeMap(duplicates, jsonHandlers, this.jsonHandlers);
        this.objectMapper = objectMapper;
    }

    private <T extends ActionHandler<String>> void initializeMap(
            Map<String, Class<?>> duplicates,
            List<T> handlers,
            Map<String, T> map
    ) {
        handlers.forEach(handler -> {
            String key = handler.key();
            Class<?> type = handler.getClass();
            Class<?> previous = duplicates.put(key, type);
            if (previous != null) {
                throw new IllegalArgumentException(
                        "Duplicate key '" + key + "' found in " + type.getSimpleName() +
                                " and " + previous.getSimpleName()
                );
            }
            map.put(key, handler);
        });
    }

    @Override
    protected boolean handle(
            String prefix,
            String payload,
            MaybeInaccessibleMessage message,
            FeedbackExecutor feedbackExecutor
    ) {
        Opt<ValuedCallbackHandler> valuedHandler = Opt.ofNullable(valuedHandlers.get(prefix));
        if (valuedHandler.isPresent()) {
            valuedHandler.get().handle(payload, message, feedbackExecutor);
            return true;
        }
        Opt<JsonCallbackHandler<?>> jsonHandler = Opt.ofNullable(jsonHandlers.get(prefix));
        if (jsonHandler.isPresent()) {
            jsonHandler.get().handle(payload, objectMapper, message, feedbackExecutor);
            return true;
        }
        return false;
    }
}
