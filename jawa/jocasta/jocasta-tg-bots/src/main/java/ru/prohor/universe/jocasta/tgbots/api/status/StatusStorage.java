package ru.prohor.universe.jocasta.tgbots.api.status;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

/**
 * @param <K> status key
 */
public interface StatusStorage<K> {
    Opt<K> getStatus(long chatId);
}
