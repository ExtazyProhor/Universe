package ru.prohor.universe.jocasta.core.collections;

import java.util.Map;

public record Pair<A, B>(A a, B b) {
    public static <A, B> Pair<A, B> wrap(Map.Entry<A, B> entry) {
        return new Pair<>(entry.getKey(), entry.getValue());
    }
}
