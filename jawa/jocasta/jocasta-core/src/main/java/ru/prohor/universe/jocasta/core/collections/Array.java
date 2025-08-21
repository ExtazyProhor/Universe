package ru.prohor.universe.jocasta.core.collections;

import java.util.Arrays;

public class Array<T> {
    private final T[] source;

    public Array(T[] source) {
        this.source = source;
    }

    @SuppressWarnings("unchecked")
    public Array<T> skip(int n) {
        return new Array<T>((T[]) Arrays.stream(source).skip(n).toArray());
    }

    public T get(int index) {
        return source[index];
    }
}
