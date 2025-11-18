package ru.prohor.universe.jocasta.core.collections.api;

import jakarta.annotation.Nonnull;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.function.Predicate;

public interface NavigableSetF<T> extends NavigableSet<T>, SetF<T> {
    BidirectionalIteratorF<T> firstElementIterator(Predicate<T> predicate);

    default BidirectionalIteratorF<T> lowerIterator(T t) {
        Comparator<? super T> cmp = comparator();
        return firstElementIterator(elem -> cmp.compare(elem, t) >= 0);
    }

    default BidirectionalIteratorF<T> higherIterator(T t) {
        Comparator<? super T> cmp = comparator();
        return firstElementIterator(elem -> cmp.compare(elem, t) > 0);
    }

    @Nonnull
    @Override
    BidirectionalIteratorF<T> iterator();

    @Nonnull
    @Override
    ComparatorF<T> comparator();

    @Nonnull
    @Override
    BidirectionalIteratorF<T> descendingIterator();

    @Nonnull
    @Override
    NavigableSetF<T> descendingSet();

    @Nonnull
    default NavigableSetF<T> headSet(T toElementExclusive) {
        return headSet(toElementExclusive, false);
    }

    @Nonnull
    @Override
    NavigableSetF<T> headSet(T toElement, boolean inclusive);

    @Nonnull
    default NavigableSetF<T> tailSet(T fromElementInclusive) {
        return tailSet(fromElementInclusive, true);
    }

    @Nonnull
    @Override
    NavigableSetF<T> tailSet(T fromElement, boolean inclusive);

    @Nonnull
    default NavigableSetF<T> subSet(T fromElementInclusive, T toElementExclusive) {
        return subSet(fromElementInclusive, true, toElementExclusive, false);
    }

    @Nonnull
    @Override
    NavigableSetF<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive);
}
