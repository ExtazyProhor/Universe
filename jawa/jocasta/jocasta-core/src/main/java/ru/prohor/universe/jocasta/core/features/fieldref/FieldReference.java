package ru.prohor.universe.jocasta.core.features.fieldref;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

@FunctionalInterface
public non-sealed interface FieldReference<T, R> extends FieldRef<T, R> {
    R get(T source);

    @Override
    default Opt<R> getO(T source) {
        return Opt.of(get(source));
    }
}
