package ru.prohor.universe.jocasta.core.collections.api;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.functional.DiFunction;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.Objects;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
@FunctionalInterface
public interface ComparatorF<T> extends DiFunction<T, T, Integer>, java.util.Comparator<T> {
    @Override
    default Integer apply(T a, T b) {
        return compare(a, b);
    }

    /** Find max among a and b */
    default T max(T a, T b) {
        if (compare(a, b) > 0)
            return a;
        return b;
    }

    default T min(T a, T b) {
        if (compare(a, b) < 0)
            return a;
        return b;
    }

    @Nonnull
    @Override
    default ComparatorF<T> thenComparing(@Nonnull java.util.Comparator<? super T> other) {
        return (o1, o2) -> {
            int r = compare(o1, o2);
            if (r != 0)
                return r;
            return other.compare(o1, o2);
        };
    }

    default ComparatorF<T> nullLow() {
        return (o1, o2) -> {
            if (o1 == null || o2 == null)
                return o1 == o2 ? 0 : o1 != null ? 1 : -1;
            return compare(o1, o2);
        };
    }

    static <T> ComparatorF<T> valueLow(final T low) {
        return (o1, o2) -> {
            if (low == null)
                return o1 == o2 ? 0 : o1 != null ? 1 : -1;
            if (Objects.equals(o1, o2))
                return 0;
            if (low.equals(o1))
                return -1;
            if (low.equals(o2))
                return 1;
            return 0;
        };
    }

    default ComparatorF<T> chainToValueLow(T low) {
        return thenComparing(valueLow(low));
    }

    default DiFunction<T, T, T> maxF() {
        return this::max;
    }

    default DiFunction<T, T, T> minF() {
        return this::min;
    }

    @SuppressWarnings("unchecked")
    default <B> ComparatorF<B> uncheckedCastC() {
        return (ComparatorF<B>) this;
    }

    @Nonnull
    @Override
    default ComparatorF<T> reversed() {
        return (a, b) -> compare(b, a);
    }

    static <T> ComparatorF<T> wrap(final java.util.Comparator<T> comparator) {
        if (comparator instanceof ComparatorF<?>) return (ComparatorF<T>) comparator;
        else return comparator::compare;
    }

    static <T> ComparatorF<T> wrap(final DiFunction<T, T, Integer> comparator) {
        if (comparator instanceof ComparatorF<?>) return (ComparatorF<T>) comparator;
        else return comparator::apply;
    }

    @SuppressWarnings("unchecked")
    static <T extends java.lang.Comparable<?>> ComparatorF<T> naturalComparator()  {
        return (o1, o2) -> {
            if (o1 == null || o2 == null || o1 == o2)
                return o1 == o2 ? 0 : o1 != null ? 1 : -1;
            return ((Comparable<Object>) o1).compareTo(o2);
        };
    }

    @SuppressWarnings("unchecked")
    static <T> ComparatorF<T> naturalComparatorUnchecked() {
        return (ComparatorF<T>) naturalComparator();
    }

    static <T> ComparatorF<T> naturalComparatorBy(MonoFunction<T, ?> f) {
        return (a, b) -> naturalComparatorUnchecked().compare(f.apply(a), f.apply(b));
    }

    static <T> ComparatorF<T> constEqualComparator() {
        return (o1, o2) -> 0;
    }
}
