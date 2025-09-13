package ru.prohor.universe.jocasta.core.functional;

import java.util.Objects;

@FunctionalInterface
public interface HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> {
    boolean test(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    static <T1, T2, T3, T4, T5, T6, T7> HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> allEquals() {
        return (t1, t2, t3, t4, t5, t6, t7) -> Objects.equals(t1, t2) &&
                Objects.equals(t2, t3) &&
                Objects.equals(t3, t4) &&
                Objects.equals(t4, t5) &&
                Objects.equals(t5, t6) &&
                Objects.equals(t6, t7);
    }

    default HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> and(
            HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> other
    ) {
        return (t1, t2, t3, t4, t5, t6, t7) -> test(t1, t2, t3, t4, t5, t6, t7) &&
                other.test(t1, t2, t3, t4, t5, t6, t7);
    }

    default HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> negate() {
        return (t1, t2, t3, t4, t5, t6, t7) -> !test(t1, t2, t3, t4, t5, t6, t7);
    }

    default HeptaPredicate<T1, T2, T3, T4, T5, T6, T7> or(
            HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> other
    ) {
        return (t1, t2, t3, t4, t5, t6, t7) -> test(t1, t2, t3, t4, t5, t6, t7) ||
                other.test(t1, t2, t3, t4, t5, t6, t7);
    }
}
