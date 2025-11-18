package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.IteratorF;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class DelegatedIterator<T> implements IteratorF<T> {
    protected Iterator<T> iterator;

    protected DelegatedIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public IteratorF<T> unmodifiable() {
        return ImmutableDelegatedIterator.wrap(iterator);
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public T next() {
        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        iterator.forEachRemaining(action);
    }

    public static <A> IteratorF<A> wrap(Iterator<A> iterator) {
        if (iterator instanceof IteratorF<?>)
            return (IteratorF<A>) iterator;
        return new DelegatedIterator<>(iterator);
    }
}
