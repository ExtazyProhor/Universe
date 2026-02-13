package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractValuedCallbackSupportImpl implements FeatureSupport<CallbackQuery> {
    private final Map<String, CallbackHandler> handlers;
    private final UnknownActionKeyHandler<CallbackQuery, String> unknownCallbackPrefixHandler;

    public AbstractValuedCallbackSupportImpl(
            List<CallbackHandler> handlers,
            UnknownActionKeyHandler<CallbackQuery, String> unknownCallbackPrefixHandler
    ) {
        this.handlers = handlers.stream().collect(Collectors.toMap(
                ActionHandler::key,
                MonoFunction.identity()
        ));
        this.unknownCallbackPrefixHandler = unknownCallbackPrefixHandler;
    }

    @Override
    public void handle(CallbackQuery callback, FeedbackExecutor feedbackExecutor) {
        String callbackData = callback.getData();
        int dotIndex = callbackData.indexOf('.');
        if (dotIndex == -1) {
            Opt.ofNullable(handlers.get(callbackData)).ifPresentOrElse(
                    handler -> handler.handle(callback.getMessage(), feedbackExecutor),
                    () -> unknownCallbackPrefixHandler.handleUnknownActionKey(
                            callback,
                            callbackData,
                            feedbackExecutor
                    )
            );
            return;
        }
        if (dotIndex != callbackData.lastIndexOf('.')) {
            // log error Callback data must have zero or one dot, but got more
            return;
        }

        String prefix = callbackData.substring(0, dotIndex);
        String payload = callbackData.substring(dotIndex + 1);

        if (!handle(prefix, payload, callback.getMessage(), feedbackExecutor))
            unknownCallbackPrefixHandler.handleUnknownActionKey(callback, callbackData, feedbackExecutor);
    }

    /**
     * Called if the callback has a value
     *
     * @param prefix           callbackHandler key
     * @param payload          callback payload
     * @param message          message from callbackQuery with meta-information
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether a suitable handler was found for the prefix
     */
    protected abstract boolean handle(
            String prefix,
            String payload,
            MaybeInaccessibleMessage message,
            FeedbackExecutor feedbackExecutor
    );
}
