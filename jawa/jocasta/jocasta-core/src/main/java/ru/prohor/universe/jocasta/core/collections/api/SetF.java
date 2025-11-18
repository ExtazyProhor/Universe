package ru.prohor.universe.jocasta.core.collections.api;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public interface SetF<T> extends CollectionF<T>, Set<T> {
    @Override
    default boolean isEmpty() {
        return CollectionF.super.isEmpty();
    }

    @Nonnull
    IteratorF<T> iterator();

    @Nonnull
    @Override
    default <R> R[] toArray(@Nonnull R[] a) {
        return CollectionF.super.toArray(a);
    }

    @Nonnull
    @Override
    default Object[] toArray() {
        return CollectionF.super.toArray();
    }

    @Override
    default boolean add(T t) {
        return CollectionF.super.add(t);
    }

    @Override
    default boolean addAll(Collection<? extends T> c) {
        return CollectionF.super.addAll(c);
    }

    /** @deprecated */
    @Override
    default boolean remove(Object o) {
        return CollectionF.super.remove(o);
    }

    default SetF<T> unique() {
        return this;
    }

    @Override
    default CollectionF<T> stableUnique() {
        return this;
    }

    /** @deprecated */
    @Override
    default boolean removeAll(Collection<?> collection) {
        boolean result = false;
        if (size() <= collection.size()) {
            Iterator<?> it = iterator();
            while (it.hasNext()) {
                if (collection.contains(it.next())) {
                    it.remove();
                    result = true;
                }
            }
        } else {
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                result = remove(it.next()) || result;
            }
        }
        return result;
    }


    /** @deprecated */
    @Override
    default boolean contains(Object o) {
        return CollectionF.super.contains(o);
    }

    /** @deprecated */
    @Override
    default boolean containsAll(Collection<?> coll) {
        return CollectionF.super.containsAll(coll);
    }

    /** @deprecated */
    @Override
    default boolean retainAll(Collection<?> c) {
        return CollectionF.super.retainAll(c);
    }

    @Override
    default void clear() {
        CollectionF.super.clear();
    }

    /** Filter */
    @Override
    default SetF<T> filter(MonoPredicate<? super T> p) {
        return iterator().filter(p).toSet();
    }

    @Override
    default SetF<T> filterNot(MonoPredicate<? super T> p) {
        return filter(p.negate());
    }

    @Override
    default SetF<T> filterNotNull() {
        return filter(Objects::nonNull);
    }

    @Override
    default <F extends T> SetF<F> filterByType(Class<F> type) {
        return filter(type::isInstance).uncheckedCast();
    }

    default SetF<T> minus1(T t) {
        if (isEmpty()) return this;

        return filterNot(a -> Objects.equals(a, t));
    }

    default SetF<T> minus(Set<T> es) {
        if (this.isEmpty() || es.isEmpty())
            return this;
        return filterNot(Cf.x(es).containsF());
    }

    default SetF<T> minus(Collection<T> es) {
        if (this.isEmpty() || es.isEmpty()) return this;

        return minus(Cf.x(es).unique());
    }

    default Tuple2<SetF<T>, SetF<T>> partition(MonoPredicate<? super T> p) {
        SetF<T> matched = Cf.hashSet();
        SetF<T> unmatched = Cf.hashSet();
        for (T t : this)
            (p.test(t) ? matched : unmatched).add(t);
        return new Tuple2<>(matched, unmatched);
    }

    default SetF<T> intersect(Set<T> b) {
        if (isEmpty()) {
            return this;
        } else if (b.isEmpty()) {
            return Cf.x(b);
        } else if (size() < b.size()) {
            return this.filter(b::contains);
        } else {
            return Cf.x(b).filter(this::containsTs);
        }
    }

    default boolean intersects(Set<T> b) {
        if (isEmpty() || b.isEmpty()) {
            return false;
        }
        if (size() < b.size()) {
            return this.exists(b::contains);
        } else {
            return Cf.x(b).exists(this::containsTs);
        }
    }

    @Override
    default SetF<T> plus1(T t) {
        if (isEmpty()) return Cf.set(t);

        SetF<T> c = Cf.hashSet();
        c.addAll(this);
        c.add(t);
        return c;
    }

    default SetF<T> plus(Collection<? extends T> elements) {
        return (SetF<T>) CollectionF.super.plus(elements);
    }

    @Override
    default SetF<T> plus(Iterator<? extends T> iterator) {
        if (!iterator.hasNext()) return this;

        if (isEmpty()) return (SetF<T>) Cf.x(iterator).toSet();

        SetF<T> c = Cf.hashSet();
        c.addAll(this);
        iterator.forEachRemaining(c::add);
        return c;
    }

    @SuppressWarnings("unchecked")
    default SetF<T> plus(T... additions) {
        return (SetF<T>) CollectionF.super.plus(additions);
    }

    default SetF<T> unmodifiable() {
        //if (this instanceof Unmodifiable) return this;
        return Cf.x(Collections.unmodifiableSet(this));
    }

    @Override
    default <F> SetF<F> uncheckedCast() {
        return this.cast();
    }

    @Override
    default <F> SetF<F> cast() {
        return (SetF<F>) CollectionF.super.<F>cast();
    }

    @Override
    default <F> SetF<F> cast(Class<F> type) {
        return (SetF<F>) CollectionF.super.cast(type);
    }
}
