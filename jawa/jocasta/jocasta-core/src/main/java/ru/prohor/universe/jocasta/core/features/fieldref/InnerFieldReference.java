package ru.prohor.universe.jocasta.core.features.fieldref;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

public record InnerFieldReference<T>(String name, String delimiter) {
    private static final String DEFAULT_DELIMITER = ".";

    public <R> InnerFieldReference<R> then(TypedFieldReference<T, R> ref) {
        return new InnerFieldReference<>(name + delimiter + ref.name(), delimiter);
    }

    public <R> InnerFieldReference<R> thenO(TypedFieldReference<T, Opt<R>> ref) {
        return new InnerFieldReference<>(name + delimiter + ref.name(), delimiter);
    }

    public static <T, R> InnerFieldReference<R> create(TypedFieldReference<T, R> ref) {
        return new InnerFieldReference<>(ref.name(), DEFAULT_DELIMITER);
    }

    public static <T, R> InnerFieldReference<R> create(TypedFieldReference<T, R> ref, String delimiter) {
        return new InnerFieldReference<>(ref.name(), delimiter);
    }

    public static <T, R> InnerFieldReference<R> createO(TypedFieldReference<T, Opt<R>> ref) {
        return new InnerFieldReference<>(ref.name(), DEFAULT_DELIMITER);
    }

    public static <T, R> InnerFieldReference<R> createO(TypedFieldReference<T, Opt<R>> ref, String delimiter) {
        return new InnerFieldReference<>(ref.name(), delimiter);
    }
}
