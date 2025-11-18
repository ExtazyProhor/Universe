package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.api.ListIteratorF;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.NoSuchElementException;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class ImmutableSingletonIterator<T> implements ListIteratorF<T> {
    private boolean hasNext = true;
    private final T t;

    public ImmutableSingletonIterator(T t) {
        this.t = t;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() {
        if (!hasNext)
            throw new NoSuchElementException();
        hasNext = false;
        return t;
    }

    @Override
    public boolean hasPrevious() {
        return !hasNext;
    }

    @Override
    public T previous() {
        if (hasNext)
            throw new NoSuchElementException();
        hasNext = true;
        return t;
    }

    @Override
    public int nextIndex() {
        return hasNext ? 0 : 1;
    }

    @Override
    public int previousIndex() {
        return hasNext ? -1 : 0;
    }

    @Override
    public void set(T T) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T T) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IteratorF<T> filter(MonoPredicate<? super T> f) {
        if (hasNext && f.test(t))
            return new ImmutableSingletonIterator<>(t);
        return new EmptyIterator<>();
    }
}
