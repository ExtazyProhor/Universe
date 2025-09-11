package ru.prohor.universe.jocasta.core.functional;

import java.util.function.BiFunction;

@FunctionalInterface
public interface DiFunction<T1, T2, R> extends BiFunction<T1, T2, R> {
    @Override
    R apply(T1 t1, T2 t2);
}
