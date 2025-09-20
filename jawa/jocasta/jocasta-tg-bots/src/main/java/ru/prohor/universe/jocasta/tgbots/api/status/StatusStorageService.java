package ru.prohor.universe.jocasta.tgbots.api.status;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

public interface StatusStorageService<StatusKey, StatusValue> {
    Opt<TgBotStatus<StatusKey, StatusValue>> getStatus(long chatId);
}
