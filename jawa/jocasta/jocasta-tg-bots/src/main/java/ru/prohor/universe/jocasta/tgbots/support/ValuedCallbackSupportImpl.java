package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
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

public class ValuedCallbackSupportImpl extends AbstractValuedCallbackSupportImpl {
    private final Map<String, ValuedCallbackHandler> valuedHandlers;

    public ValuedCallbackSupportImpl(
            List<CallbackHandler> handlers,
            List<ValuedCallbackHandler> valuedHandlers,
            TriFunction<CallbackQuery, String, FeedbackExecutor, Boolean> unknownCallbackHandler
    ) {
        super(handlers, unknownCallbackHandler);
        this.valuedHandlers = valuedHandlers.stream().collect(Collectors.toMap(
                ActionHandler::key,
                MonoFunction.identity()
        ));
    }

    @Override
    protected Opt<Boolean> handle(
            String prefix,
            String payload,
            MaybeInaccessibleMessage message,
            FeedbackExecutor feedbackExecutor
    ) {
        return Opt.ofNullable(valuedHandlers.get(prefix)).map(
                handler -> handler.handle(payload, message, feedbackExecutor)
        );
    }
}
