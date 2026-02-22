package ru.prohor.universe.jocasta.core.features.fieldref;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

public interface FieldProperties<T, R> {
    String DEFAULT_DELIMITER = ".";

    Opt<R> getO(T source);

    String name(String delimiter);

    default String name() {
        return name(DEFAULT_DELIMITER);
    }
}
