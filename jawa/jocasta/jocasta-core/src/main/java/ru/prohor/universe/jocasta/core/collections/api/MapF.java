package ru.prohor.universe.jocasta.core.collections.api;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public interface MapF<K, V> extends Map<K, V> {
    default boolean isNotEmpty() {
        return !isEmpty();
    }

    /** Get value by key */
    default Option<V> getO(K key) {
        return Option.ofNullable(get(key));
    }

    /** Get value by key */
    default Optional<V> getOptional(K key) {
        return Optional.ofNullable(get(key));
    }

    /** Get value or default */
    default V getOrElse(K key, V elseValue) {
        return getOrDefault(key, elseValue);
    }

    /** Get value or evaluate function */
    default V getOrElseApply(K key, Function0<V> valueF) {
        V value = getTs(key);
        return value == null ? valueF.apply() : value;
    }

    /** Throws if there is no entry for key */
    default V getOrThrow(K key) {
        V res = get(key);
        if (res != null) {
            return res;
        } else {
            //for case if someone overrides getO method which can return Option.of(null)
            return getO(key).get();
        }
    }

    /** Get value or throw */
    default V getOrThrow(K key, String message) {
        V res = get(key);
        if (res != null) {
            return res;
        } else {
            //for case if someone overrides getO method which can return Option.of(null)
            return getO(key).getOrThrow(message);
        }
    }

    /** Get value or throw */
    default V getOrThrow(K key, String message, Object param) {
        V res = get(key);
        if (res != null) {
            return res;
        } else {
            //for case if someone overrides getO method which can return Option.of(null)
            return getO(key).getOrThrow(message, param);
        }
    }

    /** Get value or create a message, wrap it with exception and throw */
    default V getOrThrow(K key, Function0<String> createMessage) {
        V res = get(key);
        if (res != null) {
            return res;
        } else {
            //for case if someone overrides getO method which can return Option.of(null)
            return getO(key).getOrThrow(() -> new NoSuchElementException(createMessage.apply()));
        }
    }

    /** Get value or else update */
    default V getOrElseUpdate(K key, V value) {
        return getOrElseUpdate(key, () -> value);
    }

    /** Get value or else update */
    default V getOrElseUpdate(K key, Function0<V> valueF) {
        return computeIfAbsent(key, x -> valueF.apply());
    }

    default boolean containsEntry(K key, V value) {
        return value != null
                ? Objects.equals(get(key), value)
                : getO(key).isSome(value);
    }

    default boolean containsEntry(Entry<K, V> entry) {
        return containsEntry(entry.getKey(), entry.getValue());
    }

    /**
     * @deprecated
     * use {@link #getOrThrow(Object)} instead.
     */
    default V apply(K key) {
        return getOrThrow(key);
    }

    MapF<K, V> filterKeys(Function1B<? super K> p);

    <W> MapF<K, W> mapValues(Function<? super V, ? extends W> f);

    <W> MapF<K, W> mapValuesWithKey(Function2<? super K, ? super V, W> f);

    //<W> ListF<W> map(Function<Entry<K, V>, W> f);

    <W> ListF<W> mapEntries(Function2<? super K, ? super V, ? extends W> f);

    MapF<K, V> filter(Function1B<? super Tuple2<K, V>> p);

    MapF<K, V> filter(Function2B<? super K, ? super V> p);

    MapF<K, V> filterEntries(Function1B<Entry<K, V>> p);

    MapF<K, V> filterValues(Function1B<? super V> p);

    boolean forAllEntries(Function2B<? super K, ? super V> op);

    /** @deprecated */
    default Function<K, V> asFunction() {
        return this::getOrThrow;
    }

    /** @deprecated */
    default Function<K, Option<V>> asFunctionO() {
        return this::getO;
    }

    /** @deprecated */
    default Function<K, V> asFunctionOrElse(V fallback) {
        return k -> getOrElse(k, fallback);
    }

    /** @deprecated */
    default Function<K, V> asFunctionOrElse(final Function<K, V> fallbackF) {
        return key -> getO(key).getOrElse(() -> fallbackF.apply(key));
    }

    default void put(Entry<K, V> entry) {
        put(entry.getKey(), entry.getValue());
    }

    /** Put */
    default void put(Tuple2<K, V> entry) {
        put(entry.get1(), entry.get2());
    }

    /** Put all */
    void putAll(Iterable<Tuple2<K, V>> entries);

    void putAllEntries(Iterable<Entry<K, V>> entries);

    /** Remove element. Return old */
    default Option<V> removeO(K key) {
        return Option.ofNullable(remove(key));
    }

    /** Key set */
    SetF<K> keySet();

    default ListF<K> keys() {
        return keySet().toList();
    }

    SetF<Entry<K, V>> entrySet();

    Tuple2List<K, V> entries();

    /** Values */
    CollectionF<V> values();

    MapF<K, V> plus(MapF<K, V> map);

    MapF<K, V> plus1(K key, V value);

    MapF<K, V> unmodifiable();

    <L, W> MapF<L, W> uncheckedCast();

    @Override
    boolean containsKey(Object key);

    default boolean containsKeyTs(K key) {
        return containsKey(key);
    }

    @Override
    boolean containsValue(Object value);

    default boolean containsValueTs(V value) {
        return containsValue(value);
    }

    /** @deprecated */
    @Override
    V remove(Object key);
    default V removeTs(K key) {
        return remove(key);
    }
    default V removeTu(Object key) {
        return remove(key);
    }

    /** @deprecated */
    @Override
    V get(Object key);
    default V getTs(K key) {
        return get(key);
    }
    default V getTu(Object key) {
        return get(key);
    }

    /** @deprecated inline */
    static <K, V, E> Collector<E, ?, MapF<K, V>> collector(java.util.function.Function<E, K> keys, java.util.function.Function<E, V> values) {
        return CollectorsF.toMutHashMap(keys, values);
    }
}
