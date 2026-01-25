package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

import java.util.List;

public class CallbackSupportImpl extends FeatureSupportImpl<CallbackQuery, String, CallbackHandler> {
    public CallbackSupportImpl(
            List<CallbackHandler> handlers,
            UnknownActionKeyHandler<CallbackQuery, String> unknownActionKeyHandler
    ) {
        super(handlers, unknownActionKeyHandler);
    }

    @Override
    public void handle(CallbackQuery callback, FeedbackExecutor feedbackExecutor) {
        String callbackData = callback.getData();
        if (callbackData.indexOf('.') != -1) {
            // log error simple callback must have zero dots, but got more
            return;
        }
        useHandler(
                callback,
                callbackData,
                handler -> handler.handle(callback.getMessage(), feedbackExecutor),
                feedbackExecutor
        );
    }
}
