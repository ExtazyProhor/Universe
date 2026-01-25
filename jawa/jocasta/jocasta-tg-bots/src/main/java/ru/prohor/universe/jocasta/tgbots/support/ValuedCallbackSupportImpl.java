package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;
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
            UnknownActionKeyHandler<CallbackQuery, String> unknownCallbackPrefixHandler
    ) {
        super(handlers, unknownCallbackPrefixHandler);
        this.valuedHandlers = valuedHandlers.stream().collect(Collectors.toMap(
                ActionHandler::key,
                MonoFunction.identity()
        ));
    }

    @Override
    protected boolean handle(
            String prefix,
            String payload,
            MaybeInaccessibleMessage message,
            FeedbackExecutor feedbackExecutor
    ) {
        return Opt.ofNullable(valuedHandlers.get(prefix)).map(handler -> {
            handler.handle(payload, message, feedbackExecutor);
            return true;
        }).orElse(false);
    }
}
