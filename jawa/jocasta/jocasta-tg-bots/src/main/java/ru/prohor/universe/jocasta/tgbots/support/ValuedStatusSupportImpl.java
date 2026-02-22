package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusFlow;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.UnknownStatusKeyHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @param <K> status key
 * @param <V> status value
 */
public class ValuedStatusSupportImpl<K, V> implements StatusSupport {
    private final Map<K, StatusHandler<K>> handlers;
    private final Map<K, ValuedStatusHandler<K, V>> valuedHandlers;
    private final UnknownStatusKeyHandler<K> unknownStatusKeyHandler;
    private final ValuedStatusStorage<K, V> valuedStatusStorage;

    public ValuedStatusSupportImpl(
            List<StatusHandler<K>> handlers,
            List<ValuedStatusHandler<K, V>> valuedHandlers,
            UnknownStatusKeyHandler<K> unknownStatusKeyHandler,
            ValuedStatusStorage<K, V> valuedStatusStorage
    ) {
        this.handlers = handlers.stream().collect(Collectors.toMap(ActionHandler::key, MonoFunction.identity()));
        this.valuedHandlers = new HashMap<>();
        for (ValuedStatusHandler<K, V> handler : valuedHandlers) {
            if (this.handlers.containsKey(handler.key())) {
                throw new IllegalArgumentException(handler.key() + " key is duplicated in handlers and valuedHandlers");
            }
            this.valuedHandlers.put(handler.key(), handler);
        }
        this.unknownStatusKeyHandler = unknownStatusKeyHandler;
        this.valuedStatusStorage = valuedStatusStorage;
    }

    @Override
    public StatusFlow handle(Update update, FeedbackExecutor feedbackExecutor) {
        Long nullableChatId = null;
        if (update.hasMessage())
            nullableChatId = update.getMessage().getChatId();
        else if (update.hasCallbackQuery())
            nullableChatId = update.getCallbackQuery().getMessage().getChatId();
        if (nullableChatId == null)
            return StatusFlow.CONTINUE;

        Opt<ValuedStatusStorage.ValuedStatus<K, V>> statusO = valuedStatusStorage.getStatus(nullableChatId);
        if (statusO.isEmpty())
            return StatusFlow.CONTINUE;
        ValuedStatusStorage.ValuedStatus<K, V> status = statusO.get();
        K key = status.key();

        if (status.value().isPresent()) {
            return Opt.ofNullable(valuedHandlers.get(key))
                    .map(handler -> handler.handle(status.value().get(), update, feedbackExecutor))
                    .orElseGet(() -> unknownStatusKeyHandler.handleUnknownActionKey(update, key, feedbackExecutor));
        }
        return Opt.ofNullable(handlers.get(key))
                .map(handler -> handler.handle(update, feedbackExecutor))
                .orElseGet(() -> unknownStatusKeyHandler.handleUnknownActionKey(update, key, feedbackExecutor));
    }
}
