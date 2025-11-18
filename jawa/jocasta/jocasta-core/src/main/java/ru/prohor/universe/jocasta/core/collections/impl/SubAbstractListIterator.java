package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.api.ListIteratorF;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public final class SubAbstractListIterator<T> implements ListIteratorF<T>, IteratorF<T> {
    private final SubAbstractList<T> subList;
    private final ListIterator<T> iterator;
    private final int start;

    private int end;

    SubAbstractListIterator(
            ListIterator<T> it,
            SubAbstractList<T> list, int offset, int length
    ) {
        super();
        iterator = it;
        subList = list;
        start = offset;
        end = start + length;
    }

    public void add(T object) {
        iterator.add(object);
        subList.sizeChanged(true);
        end++;
    }

    public boolean hasNext() {
        return iterator.nextIndex() < end;
    }

    public boolean hasPrevious() {
        return iterator.previousIndex() >= start;
    }

    public T next() {
        if (iterator.nextIndex() < end) {
            return iterator.next();
        }
        throw new NoSuchElementException();
    }

    public int nextIndex() {
        return iterator.nextIndex() - start;
    }

    public T previous() {
        if (iterator.previousIndex() >= start)
            return iterator.previous();
        throw new NoSuchElementException();
    }

    public int previousIndex() {
        int previous = iterator.previousIndex();
        if (previous >= start)
            return previous - start;
        return -1;
    }

    public void remove() {
        iterator.remove();
        subList.sizeChanged(false);
        end--;
    }

    public void set(T object) {
        iterator.set(object);
    }
}
