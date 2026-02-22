package ru.prohor.universe.jocasta.tgbots.api.status;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

/**
 * @param <K> status key
 * @param <V> status value
 */
public interface ValuedStatusStorage<K, V> {
    Opt<ValuedStatus<K, V>> getStatus(long chatId);

    /**
     * @param <K> status key
     * @param <V> status value
     */
    record ValuedStatus<K, V>(K key, Opt<V> value) {}
}
