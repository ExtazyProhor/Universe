package ru.prohor.universe.jocasta.core.collections.impl;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.api.ListF;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class ImmutableDelegatedList<T> extends DelegatedList<T> {
    public ImmutableDelegatedList(List<T> target) {
        super(target);
    }

    public ListF<T> unmodifiable() {
        return this;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public boolean add(T o) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("immutable");
    }

    @Nonnull
    @Override
    public ListIterator<T> listIterator() {

        // XXX: immutable ListIterator
        return super.listIterator();
    }

    @Nonnull
    @Override
    public ListIterator<T> listIterator(int index) {
        // XXX: immutable ListIterator
        return super.listIterator(index);
    }

    @Override
    public void removeRangeProtected(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("immutable");
    }

    @Override
    public ListF<T> subList(int fromIndex, int toIndex) {
        return new ImmutableDelegatedList<>(target.subList(fromIndex, toIndex));
    }

    @Override
    public IteratorF<T> iterator() {
        return new ImmutableDelegatedIterator<>(target.iterator());
    }

    public static <T> ListF<T> wrap(List<T> list) {
        if (list instanceof ListF<?> && list instanceof Unmodifiable) return (ListF<T>) list;
        else return new UnmodifiableDefaultListF<>(list);
    }
}
