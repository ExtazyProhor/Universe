package ru.prohor.universe.jocasta.tgbots.support.status;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
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
    private final Cache<Long, TgBotStatus<SK, SV>> cache;
    private final Map<SK, StatusHandler<SK, SV>> statusHandlers;

    public StatusSupportImpl(
            int statusesCacheSize,
            StatusStorageService<SK, SV> statusStorageService,
            List<StatusHandler<SK, SV>> statusHandlers
    ) {
        this.statusStorageService = statusStorageService;
        this.cache = Caffeine.newBuilder().maximumSize(statusesCacheSize).build();
        this.statusHandlers = statusHandlers.stream().collect(Collectors.toMap(
                StatusHandler::key,
                MonoFunction.identity()
        ));
    }

    @Override
    public boolean handleUpdate(Update update) {
        Long nullableChatId = null;
        if (update.hasMessage())
            nullableChatId = update.getMessage().getChatId();
        else if (update.hasCallbackQuery())
            nullableChatId = update.getCallbackQuery().getMessage().getChatId();
        if (nullableChatId == null)
            return true;

        long[] chatId = {nullableChatId};
        Opt<TgBotStatus<SK, SV>> status = Opt.ofNullable(cache.getIfPresent(chatId[0])).orElse(() -> {
            Opt<TgBotStatus<SK, SV>> statusFromStorage = statusStorageService.getStatus(chatId[0]);
            statusFromStorage.ifPresent(st -> cache.put(chatId[0], st));
            return statusFromStorage;
        });
        if (status.isEmpty())
            return true;

        return Opt.ofNullable(statusHandlers.get(status.get().key())).map(
                handler -> {
                    handler.handle(status.get().value());
                    return handler.shouldContinueProcessing();
                }
        ).orElseGet(() -> {
            // TODO log.warn
            return true;
        });
    }
}
