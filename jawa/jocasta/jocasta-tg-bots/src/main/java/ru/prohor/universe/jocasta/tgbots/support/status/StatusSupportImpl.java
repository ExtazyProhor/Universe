package ru.prohor.universe.jocasta.tgbots.support.status;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusStorageService;
import ru.prohor.universe.jocasta.tgbots.api.status.TgBotStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @param <SK> status key
 * @param <SV> status value
 */
public class StatusSupportImpl<SK, SV> implements StatusSupport {
    private final StatusStorageService<SK, SV> statusStorageService;
    private final Map<SK, StatusHandler<SK, SV>> statusHandlers;

    public StatusSupportImpl(
            StatusStorageService<SK, SV> statusStorageService,
            List<StatusHandler<SK, SV>> statusHandlers
    ) {
        this.statusStorageService = statusStorageService;
        this.statusHandlers = statusHandlers.stream().collect(Collectors.toMap(
                StatusHandler::key,
                MonoFunction.identity()
        ));
    }

    @Override
    public boolean handleUpdate(Update update, FeedbackExecutor feedbackExecutor) {
        Long nullableChatId = null;
        if (update.hasMessage())
            nullableChatId = update.getMessage().getChatId();
        else if (update.hasCallbackQuery())
            nullableChatId = update.getCallbackQuery().getMessage().getChatId();
        if (nullableChatId == null)
            return true;

        long[] chatId = {nullableChatId};
        Opt<TgBotStatus<SK, SV>> status = statusStorageService.getStatus(chatId[0]);
        if (status.isEmpty())
            return true;

        return Opt.ofNullable(statusHandlers.get(status.get().key())).map(
                handler -> handler.handle(status.get().value(), feedbackExecutor)
        ).orElseGet(() -> {
            // TODO log.warn
            return true;
        });
    }
}
