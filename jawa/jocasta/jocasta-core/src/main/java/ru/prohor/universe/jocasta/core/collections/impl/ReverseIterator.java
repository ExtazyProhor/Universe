package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.IteratorF;

import java.util.ListIterator;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class ReverseIterator<E> implements IteratorF<E> {
    private final ListIterator<E> listIterator;

    public ReverseIterator(ListIterator<E> listIterator) {
        this.listIterator = listIterator;
    }

    @Override
    public boolean hasNext() {
        return listIterator.hasPrevious();
    }

    @Override
    public E next() {
        return listIterator.previous();
    }

    @Override
    public void remove() {
        listIterator.remove();
    }
}
