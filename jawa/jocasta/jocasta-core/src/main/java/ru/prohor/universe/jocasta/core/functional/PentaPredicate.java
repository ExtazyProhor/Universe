package ru.prohor.universe.jocasta.core.functional;

import java.util.Objects;

@FunctionalInterface
public interface PentaPredicate<T1, T2, T3, T4, T5> {
    boolean test(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    static <T1, T2, T3, T4, T5> PentaPredicate<T1, T2, T3, T4, T5> allEquals() {
        return (t1, t2, t3, t4, t5) -> Objects.equals(t1, t2) &&
                Objects.equals(t2, t3) &&
                Objects.equals(t3, t4) &&
                Objects.equals(t4, t5);
    }

    default PentaPredicate<T1, T2, T3, T4, T5> and(
            PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> other
    ) {
        return (t1, t2, t3, t4, t5) -> test(t1, t2, t3, t4, t5) &&
                other.test(t1, t2, t3, t4, t5);
    }

    default PentaPredicate<T1, T2, T3, T4, T5> negate() {
        return (t1, t2, t3, t4, t5) -> !test(t1, t2, t3, t4, t5);
    }

    default PentaPredicate<T1, T2, T3, T4, T5> or(
            PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> other
    ) {
        return (t1, t2, t3, t4, t5) -> test(t1, t2, t3, t4, t5) ||
                other.test(t1, t2, t3, t4, t5);
    }
}
