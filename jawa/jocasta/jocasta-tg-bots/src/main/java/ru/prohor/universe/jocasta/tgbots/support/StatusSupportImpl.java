package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusStorage;

import java.util.List;

/**
 * @param <K> status key
 */
public class StatusSupportImpl<K> extends FeatureSupportImpl<Update, K, StatusHandler<K>> {
    private final StatusStorage<K> statusStorage;

    public StatusSupportImpl(
            StatusStorage<K> statusStorage,
            List<StatusHandler<K>> handlers,
            TriFunction<Update, K, FeedbackExecutor, Boolean> unknownKeyHandler
    ) {
        super(handlers, unknownKeyHandler);
        this.statusStorage = statusStorage;
    }

    @Override
    public boolean handle(Update update, FeedbackExecutor feedbackExecutor) {
        Long nullableChatId = null;
        if (update.hasMessage())
            nullableChatId = update.getMessage().getChatId();
        else if (update.hasCallbackQuery())
            nullableChatId = update.getCallbackQuery().getMessage().getChatId();
        if (nullableChatId == null)
            return true;

        Opt<K> status = statusStorage.getStatus(nullableChatId);
        if (status.isEmpty())
            return true;

        return useHandler(
                update,
                status.get(),
                handler -> handler.handle(update, feedbackExecutor),
                feedbackExecutor
        );
    }
}
