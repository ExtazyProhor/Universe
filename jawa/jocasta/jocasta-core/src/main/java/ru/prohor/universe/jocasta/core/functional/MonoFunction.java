package ru.prohor.universe.jocasta.core.functional;

import java.util.function.Function;

@FunctionalInterface
public interface MonoFunction<T1, R> extends Function<T1, R> {
    @Override
    R apply(T1 t1);

    static <T> MonoFunction<T, T> identity() {
        return t -> t;
    }
}
