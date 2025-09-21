package ru.prohor.universe.jocasta.tgbots.support;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.DiFunction;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class FeatureSupportImpl<T, K, H extends ActionHandler<K>> implements FeatureSupport<T> {
    private final Map<K, H> handlers;
    private final DiFunction<K, FeedbackExecutor, Boolean> unknownKeyHandler;

    public FeatureSupportImpl(
            List<H> handlers,
            DiFunction<K, FeedbackExecutor, Boolean> unknownKeyHandler
    ) {
        this.handlers = handlers.stream().collect(Collectors.toMap(ActionHandler::key, MonoFunction.identity()));
        this.unknownKeyHandler = unknownKeyHandler;
    }

    protected final boolean useHandler(
            K key,
            MonoFunction<H, Boolean> handlerExecutor,
            FeedbackExecutor feedbackExecutor
    ) {
        return Opt.ofNullable(handlers.get(key)).map(handlerExecutor).orElseGet(
                () -> unknownKeyHandler.apply(key, feedbackExecutor)
        );
    }
}
