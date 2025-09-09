package ru.prohor.universe.jocasta.core.features.sneaky;

@FunctionalInterface
public interface ThrowableSupplier<T> {
    T get() throws Exception;
}
