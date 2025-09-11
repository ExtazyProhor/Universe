package ru.prohor.universe.jocasta.core.functional;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface DiConsumer<T1, T2> extends BiConsumer<T1, T2> {
    @Override
    void accept(T1 t1, T2 t2);
}
