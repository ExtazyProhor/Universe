package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.BotStatus;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusStorageService;

import java.util.List;

/**
 * @param <K> status key
 * @param <V> status value
 */
public class StatusSupportImpl<K, V> extends FeatureSupportImpl<Update, K, StatusHandler<K, V>> {
    private final StatusStorageService<K, V> statusStorageService;

    public StatusSupportImpl(
            StatusStorageService<K, V> statusStorageService,
            List<StatusHandler<K, V>> handlers,
            TriFunction<Update, K, FeedbackExecutor, Boolean> unknownKeyHandler
    ) {
        super(handlers, unknownKeyHandler);
        this.statusStorageService = statusStorageService;
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

        long[] chatId = {nullableChatId};
        Opt<BotStatus<K, V>> status = statusStorageService.getStatus(chatId[0]);
        if (status.isEmpty())
            return true;

        return useHandler(
                update,
                status.get().key(),
                handler -> handler.handle(status.get().value(), update, feedbackExecutor),
                feedbackExecutor
        );
    }
}
