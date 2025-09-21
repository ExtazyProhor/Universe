package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

import java.util.List;

public class CallbackSupportImpl extends FeatureSupportImpl<CallbackQuery, String, CallbackHandler> {
    public CallbackSupportImpl(
            List<CallbackHandler> handlers,
            TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler
    ) {
        super(handlers, unknownCallbackHandler);
    }

    @Override
    public boolean handle(CallbackQuery callback, FeedbackExecutor feedbackExecutor) {
        String callbackData = callback.getData();
        int dotIndex = callbackData.lastIndexOf('.');
        if (dotIndex != callbackData.lastIndexOf('.'))
            // log error Callback data must have zero or one dot, but got callbackData
            return true;
        String prefix = dotIndex == -1 ? callbackData : callbackData.substring(0, dotIndex);
        Opt<String> payload = Opt.when(dotIndex != -1, () -> callbackData.substring(dotIndex + 1));
        return useHandler(callback, prefix, handler -> handler.handle(payload, feedbackExecutor), feedbackExecutor);
    }
}
