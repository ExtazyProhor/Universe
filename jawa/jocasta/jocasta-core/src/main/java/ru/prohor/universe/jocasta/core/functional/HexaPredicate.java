package ru.prohor.universe.jocasta.core.functional;

import java.util.Objects;

@FunctionalInterface
public interface HexaPredicate<T1, T2, T3, T4, T5, T6> {
    boolean test(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    static <T1, T2, T3, T4, T5, T6> HexaPredicate<T1, T2, T3, T4, T5, T6> allEquals() {
        return (t1, t2, t3, t4, t5, t6) -> Objects.equals(t1, t2) &&
                Objects.equals(t2, t3) &&
                Objects.equals(t3, t4) &&
                Objects.equals(t4, t5) &&
                Objects.equals(t5, t6);
    }
}
