package ru.prohor.universe.jocasta.core.collections.common;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
    private T value;
    private boolean created = false;
    private final Supplier<T> loader;

    public Lazy(T value) {
        this.loader = () -> value;
    }

    public Lazy(Supplier<T> loader) {
        this.loader = loader;
    }

    @Override
    public T get() {
        if (created)
            return value;
        created = true;
        return value = loader.get();
    }

    public boolean wasCreated() {
        return created;
    }
}
