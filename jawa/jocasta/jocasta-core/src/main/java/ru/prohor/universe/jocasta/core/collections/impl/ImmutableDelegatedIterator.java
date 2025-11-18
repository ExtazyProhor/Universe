package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.IteratorF;

import java.util.Iterator;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class ImmutableDelegatedIterator<T> extends DelegatedIterator<T> {
    protected ImmutableDelegatedIterator(Iterator<T> target) {
        super(target);
    }

    @Override
    public IteratorF<T> immutable() {
        return this;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("immutable");
    }

    public static <T> IteratorF<T> wrap(Iterator<T> iterator) {
        if (iterator instanceof ImmutableDelegatedIterator<?>)
            return (IteratorF<T>) iterator;
        return new ImmutableDelegatedIterator<>(iterator);
    }
}
