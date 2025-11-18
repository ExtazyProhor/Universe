package ru.prohor.universe.jocasta.core.collections.api;

import java.util.Queue;

public interface QueueF<T> extends CollectionF<T>, Queue<T> {
    @Override
    default boolean add(T t) {
        return CollectionF.super.add(t);
    }

    @Override
    default boolean offer(T t) {
        return add(t);
    }

    @Override
    default boolean remove(Object o) {
        return CollectionF.super.remove(o);
    }

    @Override
    default boolean removeTs(T t) {
        return remove(t);
    }

    @Override
    default T poll() {

    }
}
