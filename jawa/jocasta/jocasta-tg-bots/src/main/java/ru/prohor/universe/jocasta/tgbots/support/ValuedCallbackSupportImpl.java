package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.ValuedCallbackHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValuedCallbackSupportImpl implements FeatureSupport<CallbackQuery> {
    private final Map<String, CallbackHandler> handlers;
    private final Map<String, ValuedCallbackHandler> valuedHandlers;
    private final TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler;

    public ValuedCallbackSupportImpl(
            List<CallbackHandler> handlers,
            List<ValuedCallbackHandler> valuedHandlers,
            TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler
    ) {
        this.handlers = handlers.stream().collect(Collectors.toMap(
                ActionHandler::key,
                MonoFunction.identity()
        ));
        this.valuedHandlers = valuedHandlers.stream().collect(Collectors.toMap(
                ActionHandler::key,
                MonoFunction.identity()
        ));
        this.unknownCallbackHandler = unknownCallbackHandler;
    }

    @Override
    public boolean handle(CallbackQuery callback, FeedbackExecutor feedbackExecutor) {
        String callbackData = callback.getData();
        int dotIndex = callbackData.indexOf('.');
        if (dotIndex == -1) {
            return Opt.ofNullable(handlers.get(callbackData))
                    .map(handler -> handler.handle(callback.getMessage(), feedbackExecutor))
                    .orElseGet(() -> unknownCallbackHandler.apply(callback, callbackData, feedbackExecutor));
        }
        if (dotIndex != callbackData.lastIndexOf('.')) {
            // log error Callback data must have zero or one dot, but got more
            return true;
        }

        String prefix = callbackData.substring(0, dotIndex);
        String payload = callbackData.substring(dotIndex + 1);
        return Opt.ofNullable(valuedHandlers.get(prefix))
                .map(handler -> handler.handle(payload, callback.getMessage(), feedbackExecutor))
                .orElseGet(() -> unknownCallbackHandler.apply(callback, callbackData, feedbackExecutor));
    }
}
