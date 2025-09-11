package ru.prohor.universe.jocasta.core.functional;

import java.util.function.Consumer;

@FunctionalInterface
public interface MonoConsumer<T1> extends Consumer<T1> {
    @Override
    void accept(T1 t1);
}
