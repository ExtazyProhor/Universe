package ru.prohor.universe.jocasta.core.collections;

import java.util.function.UnaryOperator;

public class Mutable<T> {
    private T value;

    public Mutable(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public T set(T value) {
        return this.value = value;
    }

    public T calculate(UnaryOperator<T> calculator) {
        return set(calculator.apply(value));
    }
}
