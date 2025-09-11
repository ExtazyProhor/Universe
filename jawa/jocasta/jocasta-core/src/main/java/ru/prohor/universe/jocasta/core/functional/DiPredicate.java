package ru.prohor.universe.jocasta.core.functional;

import java.util.Objects;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface DiPredicate<T1, T2> extends BiPredicate<T1, T2> {
    @Override
    boolean test(T1 t1, T2 t2);

    static <T1, T2> DiPredicate<T1, T2> allEquals() {
        return Objects::equals;
    }
}
