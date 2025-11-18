package ru.prohor.universe.jocasta.core.collections.api;

import java.util.ListIterator;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public interface ListIteratorF<T> extends IteratorF<T>, ListIterator<T> {
    @Override
    default void remove() {
        IteratorF.super.remove();
    }
}
