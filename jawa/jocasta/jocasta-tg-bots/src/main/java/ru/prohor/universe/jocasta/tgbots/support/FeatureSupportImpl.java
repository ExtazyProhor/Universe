package ru.prohor.universe.jocasta.tgbots.support;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoConsumer;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.UnknownActionKeyHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class FeatureSupportImpl<T, K, H extends ActionHandler<K>> implements FeatureSupport<T> {
    private final Map<K, H> handlers;
    private final UnknownActionKeyHandler<T, K> unknownActionKeyHandler;

    public FeatureSupportImpl(
            List<H> handlers,
            UnknownActionKeyHandler<T, K> unknownActionKeyHandler
    ) {
        this.handlers = handlers.stream().collect(Collectors.toMap(ActionHandler::key, MonoFunction.identity()));
        this.unknownActionKeyHandler = unknownActionKeyHandler;
    }

    protected final void useHandler(
            T object,
            K key,
            MonoConsumer<H> handlerExecutor,
            FeedbackExecutor feedbackExecutor
    ) {
        Opt.ofNullable(handlers.get(key)).ifPresentOrElse(
                handlerExecutor,
                () -> unknownActionKeyHandler.handleUnknownActionKey(object, key, feedbackExecutor)
        );
    }
}
