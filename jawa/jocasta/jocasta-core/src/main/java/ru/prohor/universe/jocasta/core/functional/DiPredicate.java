package ru.prohor.universe.jocasta.core.functional;

import java.util.Objects;

@FunctionalInterface
public interface DiPredicate<T1, T2> {
    boolean test(T1 t1, T2 t2);

    static <T1, T2> DiPredicate<T1, T2> allEquals() {
        return Objects::equals;
    }

    default DiPredicate<T1, T2> and(DiPredicate<? super T1, ? super T2> other) {
        return (t1, t2) -> test(t1, t2) && other.test(t1, t2);
    }

    default DiPredicate<T1, T2> negate() {
        return (t1, t2) -> !test(t1, t2);
    }

    default DiPredicate<T1, T2> or(DiPredicate<? super T1, ? super T2> other) {
        return (t1, t2) -> test(t1, t2) || other.test(t1, t2);
    }
}
