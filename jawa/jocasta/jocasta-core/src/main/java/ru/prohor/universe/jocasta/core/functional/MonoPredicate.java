package ru.prohor.universe.jocasta.core.functional;

import java.util.function.Predicate;

@FunctionalInterface
public interface MonoPredicate<T1> extends Predicate<T1> {
    boolean test(T1 t1);

    default MonoPredicate<T1> and(MonoPredicate<? super T1> other) {
        return (t1) -> test(t1) && other.test(t1);
    }

    default MonoPredicate<T1> negate() {
        return (t1) -> !test(t1);
    }

    default MonoPredicate<T1> or(MonoPredicate<? super T1> other) {
        return (t1) -> test(t1) || other.test(t1);
    }
}
