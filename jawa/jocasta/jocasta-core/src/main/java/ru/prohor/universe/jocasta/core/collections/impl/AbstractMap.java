package ru.prohor.universe.jocasta.core.collections.impl;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.api.CollectionF;
import ru.prohor.universe.jocasta.core.collections.api.MapF;
import ru.prohor.universe.jocasta.core.collections.api.SetF;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;

public abstract class AbstractMap<K, V> extends java.util.AbstractMap<K, V> implements MapF<K, V> {

    @Nonnull
    @Override
    public SetF<K> keySet() {
        return Cf.x(super.keySet());
    }

    @Override
    public abstract SetF<Entry<K, V>> entrySet();

    @Override
    public Tuple2List<K, V> entries() {
        return Tuple2List.tuple2List(entrySet().map(e -> Tuple2.tuple(e.getKey(), e.getValue())));
    }

    @Override
    public CollectionF<V> values() {
        return Cf.x(super.values());
    }

    protected <A, B> MapF<A, B> newMapFromTuples(CollectionF<Tuple2<A, B>> entries) {
        MapF<A, B> r = newMutableMap();
        r.putAll(entries);
        return r;
    }

    protected <A, B> MapF<A, B> newMutableMap() {
        return Cf.hashMap();
    }

    @SuppressWarnings("unchecked")
    @Override
    public MapF<K, V> filterKeys(final Function1B<? super K> p) {
        return filterEntries(e -> p.apply(e.getKey()));
    }

    @Override
    public MapF<K, V> filterEntries(Function1B<Entry<K, V>> p) {
        if (isEmpty()) return this;

        MapF<K, V> result = newMutableMap();
        for (Entry<K, V> e : entrySet()) {
            if (p.apply(e)) result.put(e.getKey(), e.getValue());
        }
        return result;
    }

    @Override
    public MapF<K, V> filter(final Function1B<? super Tuple2<K, V>> p) {
        return filterEntries(entry -> p.apply(Tuple2.tuple(entry.getKey(), entry.getValue())));
    }

    @Override
    public MapF<K, V> filter(final Function2B<? super K, ? super V> p) {
        return filterEntries(e -> p.apply(e.getKey(), e.getValue()));
    }

    @Override
    public MapF<K, V> filterValues(Function1B<? super V> p) {
        return filterEntries(e -> p.apply(e.getValue()));
    }

    @Override
    public boolean forAllEntries(Function2B<? super K, ? super V> op) {
        return entrySet().forAll(e -> op.apply(e.getKey(), e.getValue()));
    }

    @Override
    public MapF<K, V> unmodifiable() {
        return UnmodifiableDefaultMapF.wrap(this);
    }

    public <W> MapF<K, W> mapValues(final Function<? super V, ? extends W> f) {
        return mapValuesWithKey((k, v) -> f.apply(v));
    }

    @Override
    public <W> MapF<K, W> mapValuesWithKey(Function2<? super K, ? super V, W> f) {
        if (isEmpty()) return Cf.map();
        MapF<K, W> result = newMutableMap();
        forEach((k, v) -> result.put(k, f.apply(k, v)));
        return result;
    }

    public <W> ListF<W> mapEntries(final Function2<? super K, ? super V, ? extends W> f) {
        return entrySet().map(entry -> f.apply(entry.getKey(), entry.getValue()));
    }

    /** Must check for non-null arguments */
    public V put(K key, V value) {
        throw new UnsupportedOperationException("readonly map");
    }

    public MapF<K, V> plus(MapF<K, V> map) {
        if (map.isEmpty()) return this;
        else if (this.isEmpty()) return map;
        else {
            MapF<K, V> result = newMutableMap();
            result.putAll(this);
            result.putAll(map);
            return result;
        }
    }

    public MapF<K, V> plus1(K key, V value) {
        return plus(Cf.map(key, value));
    }

    @Override
    public void putAll(Iterable<Tuple2<K, V>> entries) {
        for (Tuple2<K, V> value : entries) {
            put(value);
        }
    }

    @Override
    public void putAllEntries(Iterable<Entry<K, V>> entries) {
        for (Entry<K, V> entry : entries) {
            put(entry);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <L, W> MapF<L, W> uncheckedCast() {
        return (MapF<L, W>) this;
    }

    @Override
    public boolean containsKeyTs(K key) {
        return containsKey(key);
    }
}
