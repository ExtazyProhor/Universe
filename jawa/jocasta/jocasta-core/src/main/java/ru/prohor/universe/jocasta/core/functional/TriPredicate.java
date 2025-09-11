package ru.prohor.universe.jocasta.core.functional;

import java.util.Objects;

@FunctionalInterface
public interface TriPredicate<T1, T2, T3> {
    boolean test(T1 t1, T2 t2, T3 t3);

    static <T1, T2, T3> TriPredicate<T1, T2, T3> allEquals() {
        return (t1, t2, t3) -> Objects.equals(t1, t2) && Objects.equals(t2, t3);
    }
}
