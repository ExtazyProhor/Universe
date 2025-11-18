package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.NoSuchElementException;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public class EmptyIterator<T> implements /*List*/IteratorF<T> {
    public boolean hasNext() {
        return false;
    }

    public T next() {
        throw new NoSuchElementException("next() used on EmptyIterator");
    }

    /*@Override
    public boolean hasPrevious() {
        return false;
    }*/

    /*@Override
    public T previous() {
        throw new NoSuchElementException("EmptyIterator.previous()");
    }*/

    /*@Override
    public int nextIndex() {
        return 0;
    }*/

    /*@Override
    public int previousIndex() {
        return -1;
    }*/

    /*@Override
    public void set(T T) {
        throw new UnsupportedOperationException("set() used on EmptyIterator");
    }

    @Override
    public void add(T T) {
        throw new UnsupportedOperationException("add() used on EmptyIterator");
    }*/

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() used on EmptyIterator");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <B> IteratorF<B> map(MonoFunction<? super T, B> f) {
        return (IteratorF<B>) this;
    }

    /*@Override
    public <B> IteratorF<B> flatMap(Function<? super T, ? extends Iterator<B>> f) {
        return Cf.emptyIterator();
    }*/

    @Override
    public IteratorF<T> filter(MonoPredicate<? super T> f) {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IteratorF<Tuple2<T, Integer>> zipWithIndex() {
        return (IteratorF<Tuple2<T, Integer>>) this;
    }

    /*@Override
    public ListF<T> toList() {
        return Cf.list();
    }

    @Override
    public SetF<T> toSet() {
        return Cf.set();
    }*/
}
