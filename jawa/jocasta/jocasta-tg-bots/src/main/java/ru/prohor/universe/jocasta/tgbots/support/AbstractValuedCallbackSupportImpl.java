package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractValuedCallbackSupportImpl implements FeatureSupport<CallbackQuery> {
    private final Map<String, CallbackHandler> handlers;
    private final TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler;

    public AbstractValuedCallbackSupportImpl(
            List<CallbackHandler> handlers,
            TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler
    ) {
        this.handlers = handlers.stream().collect(Collectors.toMap(
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
        return handle(prefix, payload, callback.getMessage(), feedbackExecutor).orElseGet(
                () -> unknownCallbackHandler.apply(callback, callbackData, feedbackExecutor)
        );
    }

    /**
     * Called if the callback has a value
     *
     * @param prefix           callbackHandler key
     * @param payload          callback payload
     * @param message          message from callbackQuery with meta-information
     * @param feedbackExecutor interface for sending feedback to users
     * @return Opt.empty() if no handler is found for the prefix;
     * otherwise, Opt of the flag indicating whether to continue processing the update
     */
    protected abstract Opt<Boolean> handle(
            String prefix,
            String payload,
            MaybeInaccessibleMessage message,
            FeedbackExecutor feedbackExecutor
    );
}
