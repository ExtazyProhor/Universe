package ru.prohor.universe.jocasta.core.functional;

import java.util.Objects;

@FunctionalInterface
public interface TriPredicate<T1, T2, T3> {
    boolean test(T1 t1, T2 t2, T3 t3);

    static <T1, T2, T3> TriPredicate<T1, T2, T3> allEquals() {
        return (t1, t2, t3) -> Objects.equals(t1, t2) && Objects.equals(t2, t3);
    }

    default TriPredicate<T1, T2, T3> and(TriPredicate<? super T1, ? super T2, ? super T3> other) {
        return (t1, t2, t3) -> test(t1, t2, t3) && other.test(t1, t2, t3);
    }

    default TriPredicate<T1, T2, T3> negate() {
        return (t1, t2, t3) -> !test(t1, t2, t3);
    }

    default TriPredicate<T1, T2, T3> or(TriPredicate<? super T1, ? super T2, ? super T3> other) {
        return (t1, t2, t3) -> test(t1, t2, t3) || other.test(t1, t2, t3);
    }
}
