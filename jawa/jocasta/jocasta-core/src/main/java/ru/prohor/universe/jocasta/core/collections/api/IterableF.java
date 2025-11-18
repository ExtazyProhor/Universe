package ru.prohor.universe.jocasta.core.collections.api;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.DiFunction;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public interface IterableF<T> extends Iterable<T> {
    @Nonnull
    @Override
    IteratorF<T> iterator();

    default Stream<T> stream() {
        return iterator().stream();
    }

    default boolean logicalAnd(MonoPredicate<? super T> predicate) {
        for (T t : this)
            if (!predicate.test(t))
                return false;
        return true;
    }

    default boolean logicalOr(MonoPredicate<? super T> predicate) {
        for (T t : this)
            if (predicate.test(t))
                return true;
        return false;
    }

    default Opt<T> find(MonoPredicate<? super T> predicate) {
        for (T t : this)
            if (predicate.test(t))
                return Opt.of(t);
        return Opt.empty();
    }

    default Opt<T> findNot(MonoPredicate<? super T> predicate) {
        return find(predicate.negate());
    }

    default int count(MonoPredicate<? super T> p) {
        int count = 0;
        for (T t : this)
            if (p.test(t))
                ++count;
        return count;
    }

    default <B> B foldLeft(B acc, DiFunction<? super B, ? super T, B> folder) {
        for (T t : this)
            acc = folder.apply(acc, t);
        return acc;
    }

    default T reduceLeft(DiFunction<T, T, T> reducer) {
        return reduceLeftO(reducer).orElseThrow(() -> new IllegalStateException("empty reduceLeft"));
    }

    default Opt<T> reduceLeftO(DiFunction<T, T, T> f) {
        IteratorF<T> i = iterator();
        if (i.hasNext())
            return Opt.of(i.foldLeft(i.next(), f));
        return Opt.empty();
    }

    default T min() {
        return min(ComparatorF.naturalComparatorUnchecked());
    }

    @SuppressWarnings("unchecked")
    default T min(Comparator<? super T> comparator) {
        return reduceLeft(ComparatorF.wrap((Comparator<T>) comparator)::min);
    }

    default T minBy(MonoFunction<? super T, ?> f) {
        return min(ComparatorF.naturalComparatorBy(f).nullLow());
    }

    default Opt<T> minO() {
        return minO(ComparatorF.naturalComparatorUnchecked());
    }

    default Opt<T> minO(Comparator<? super T> comparator) {
        return Opt.when(iterator().hasNext(), () -> min(comparator));
    }

    default Opt<T> minByO(MonoFunction<? super T, ?> f) {
        return Opt.when(iterator().hasNext(), () -> minBy(f));
    }

    default T max() {
        return max(ComparatorF.naturalComparatorUnchecked());
    }

    @SuppressWarnings("unchecked")
    default T max(Comparator<? super T> comparator) {
        return reduceLeft(ComparatorF.wrap((Comparator<T>) comparator)::max);
    }

    default T maxBy(MonoFunction<? super T, ?> f) {
        return max(ComparatorF.naturalComparatorBy(f).nullLow());
    }

    default Opt<T> maxO() {
        return maxO(ComparatorF.naturalComparatorUnchecked());
    }

    default Opt<T> maxO(Comparator<? super T> comparator) {
        return Opt.when(iterator().hasNext(), () -> max(comparator));
    }

    default Opt<T> maxByO(MonoFunction<? super T, ?> f) {
        return Opt.when(iterator().hasNext(), () -> maxBy(f));
    }

    default String join() {
        return join(", ");
    }

    default String join(String separator) {
        return join(separator, "", "");
    }

    default String join(String separator, String prefix, String postfix) {
        return stream()
                .map(Object::toString)
                .collect(Collectors.joining(separator, prefix, postfix));
    }

    @SuppressWarnings("unchecked")
    default <F> IterableF<F> uncheckedCast() {
        return (IterableF<F>) this;
    }
}
