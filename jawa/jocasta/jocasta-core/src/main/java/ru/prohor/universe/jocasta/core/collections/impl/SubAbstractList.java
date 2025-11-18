package ru.prohor.universe.jocasta.core.collections.impl;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.api.ListIteratorF;

import java.util.Collection;
import java.util.ConcurrentModificationException;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class SubAbstractList<T> extends AbstractList<T> {
    private final AbstractList<T> fullList;
    private final int offset;

    private int size;

    SubAbstractList(AbstractList<T> list, int start, int end) {
        super();
        fullList = list;
        modCount = fullList.modCount;
        offset = start;
        size = end - start;
    }

    @Override
    public void add(int location, T object) {
        if (modCount != fullList.modCount)
            throw new ConcurrentModificationException();
        if (location < 0 || location > size)
            throw new IndexOutOfBoundsException();
        fullList.add(location + offset, object);
        size++;
        modCount = fullList.modCount;
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        if (modCount != fullList.modCount)
            throw new ConcurrentModificationException();
        if (location < 0 || location > size)
            throw new IndexOutOfBoundsException();
        boolean result = fullList.addAll(location + offset, collection);
        if (result) {
            size += collection.size();
            modCount = fullList.modCount;
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        if (modCount != fullList.modCount)
            throw new ConcurrentModificationException();
        boolean result = fullList.addAll(offset + size, collection);
        if (result) {
            size += collection.size();
            modCount = fullList.modCount;
        }
        return result;
    }

    @Override
    public T get(int location) {
        if (modCount != fullList.modCount)
            throw new ConcurrentModificationException();
        if (location < 0 || location >= size)
            throw new IndexOutOfBoundsException();
        return fullList.get(location + offset);
    }

    @Nonnull
    @Override
    public IteratorF<T> iterator() {
        return listIterator(0);
    }

    @Nonnull
    @Override
    public ListIteratorF<T> listIterator(int location) {
        if (modCount != fullList.modCount)
            throw new ConcurrentModificationException();
        if (location < 0 || location > size)
            throw new IndexOutOfBoundsException();
        return new SubAbstractListIterator<>(fullList.listIterator(location + offset), this, offset, size);
    }

    @Override
    public T remove(int location) {
        if (modCount != fullList.modCount)
            throw new ConcurrentModificationException();
        if (location < 0 || location >= size)
            throw new IndexOutOfBoundsException();
        T result = fullList.remove(location + offset);
        size--;
        modCount = fullList.modCount;
        return result;
    }

    @Override
    public void removeRangeProtected(int start, int end) {
        if (start == end)
            return;
        if (modCount != fullList.modCount)
            throw new ConcurrentModificationException();
        fullList.removeRangeProtected(start + offset, end + offset);
        size -= end - start;
        modCount = fullList.modCount;
    }

    @Override
    public T set(int location, T object) {
        if (modCount != fullList.modCount)
            throw new ConcurrentModificationException();
        if (location < 0 || location >= size)
            throw new IndexOutOfBoundsException();
        return fullList.set(location + offset, object);
    }

    @Override
    public int size() {
        return size;
    }

    void sizeChanged(boolean increment) {
        if (increment)
            size++;
        else
            size--;
        modCount = fullList.modCount;
    }
}
