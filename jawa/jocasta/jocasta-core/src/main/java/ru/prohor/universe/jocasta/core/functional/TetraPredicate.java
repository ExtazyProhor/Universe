package ru.prohor.universe.jocasta.core.functional;

import java.util.Objects;

@FunctionalInterface
public interface TetraPredicate<T1, T2, T3, T4> {
    boolean test(T1 t1, T2 t2, T3 t3, T4 t4);

    static <T1, T2, T3, T4> TetraPredicate<T1, T2, T3, T4> allEquals() {
        return (t1, t2, t3, t4) -> Objects.equals(t1, t2) && Objects.equals(t2, t3) && Objects.equals(t3, t4);
    }
}
