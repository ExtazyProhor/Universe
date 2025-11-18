package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.NoSuchElementException;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public abstract class AbstractPrefetchingIterator<T> implements IteratorF<T> {
    private Opt<T> next = Opt.empty();
    private boolean eof = false;

    private void fill() {
        while (!eof && !next.isPresent()) {
            next = fetchNext();
            eof = !next.isPresent();
        }
    }

    @Override
    public boolean hasNext() {
        fill();
        return !eof && next.isPresent();
    }

    @Override
    public T next() {
        if (!hasNext())
            throw new NoSuchElementException("next on empty iterator");
        T t = next.get();
        next = Opt.empty();
        return t;
    }

    protected abstract Opt<T> fetchNext();
}
