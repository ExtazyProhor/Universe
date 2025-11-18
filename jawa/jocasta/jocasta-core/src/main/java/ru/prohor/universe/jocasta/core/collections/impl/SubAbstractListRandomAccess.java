package ru.prohor.universe.jocasta.core.collections.impl;

import java.util.RandomAccess;

public final class SubAbstractListRandomAccess<T> extends SubAbstractList<T> implements RandomAccess {
    SubAbstractListRandomAccess(AbstractList<T> list, int start, int end) {
        super(list, start, end);
    }
}
