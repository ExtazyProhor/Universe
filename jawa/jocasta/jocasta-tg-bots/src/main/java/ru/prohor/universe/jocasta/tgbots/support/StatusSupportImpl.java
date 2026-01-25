package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusFlow;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusStorage;
import ru.prohor.universe.jocasta.tgbots.api.status.UnknownStatusKeyHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @param <K> status key
 */
public class StatusSupportImpl<K> implements StatusSupport {
    private final UnknownStatusKeyHandler<K> unknownStatusKeyHandler;
    private final Map<K, StatusHandler<K>> handlers;
    private final StatusStorage<K> statusStorage;

    public StatusSupportImpl(
            UnknownStatusKeyHandler<K> unknownStatusKeyHandler,
            List<StatusHandler<K>> handlers,
            StatusStorage<K> statusStorage
    ) {
        this.unknownStatusKeyHandler = unknownStatusKeyHandler;
        this.handlers = handlers.stream().collect(Collectors.toMap(ActionHandler::key, MonoFunction.identity()));
        this.statusStorage = statusStorage;
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

        Opt<K> statusO = statusStorage.getStatus(nullableChatId);
        if (statusO.isEmpty())
            return StatusFlow.CONTINUE;

        K status = statusO.get();
        return Opt.ofNullable(handlers.get(status))
                .map(handler -> handler.handle(update, feedbackExecutor))
                .orElseGet(() -> unknownStatusKeyHandler.handleUnknownActionKey(update, status, feedbackExecutor));
    }
}
