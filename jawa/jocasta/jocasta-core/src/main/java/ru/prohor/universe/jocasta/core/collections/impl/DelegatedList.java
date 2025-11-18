package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.api.ListF;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class DelegatedList<T> extends AbstractList<T> {
    protected List<T> target;

    public DelegatedList(List<T> target) {
        if (target == null)
            throw new IllegalArgumentException("Delegated list is null");
        this.target = target;
    }

    @Override
    public ListF<T> immutable() {
        return ImmutableDelegatedList.wrap(target);
    }

    public int size() {
        return target.size();
    }

    public boolean isEmpty() {
        return target.isEmpty();
    }

    public boolean contains(Object o) {
        return target.contains(o);
    }

    public IteratorF<T> iterator() {
        return Cf.x(target.iterator());
    }

    public Object[] toArray() {
        return target.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return target.toArray(a);
    }

    public boolean add(T o) {
        return target.add(o);
    }

    public boolean remove(Object o) {
        return target.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return target.containsAll(c);
    }

    public boolean addAll(Collection<? extends T> c) {
        return target.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        return target.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return target.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return target.retainAll(c);
    }

    public void clear() {
        target.clear();
    }

    public boolean equals(Object o) {
        return target.equals(o);
    }

    public int hashCode() {
        return target.hashCode();
    }

    public T get(int index) {
        return target.get(index);
    }

    public T set(int index, T element) {
        return target.set(index, element);
    }

    public void add(int index, T element) {
        target.add(index, element);
    }

    public T remove(int index) {
        return target.remove(index);
    }

    public int indexOf(Object o) {
        return target.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return target.lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return target.listIterator();
    }

    public ListIterator<T> listIterator(int index) {
        return target.listIterator(index);
    }

    public ListF<T> subList(int fromIndex, int toIndex) {
        return wrap(target.subList(fromIndex, toIndex));
    }

    public static <A> ListF<A> wrap(List<A> list) {
        if (list instanceof ListF<?>)
            return (ListF<A>) list;
        else return new DelegatedList<>(list);
    }
}
