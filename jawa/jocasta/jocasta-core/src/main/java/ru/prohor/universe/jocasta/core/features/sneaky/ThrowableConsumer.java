package ru.prohor.universe.jocasta.core.features.sneaky;

@FunctionalInterface
public interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;
}
