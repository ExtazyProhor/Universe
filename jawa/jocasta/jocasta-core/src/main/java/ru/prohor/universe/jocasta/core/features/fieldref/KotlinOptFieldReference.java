package ru.prohor.universe.jocasta.core.features.fieldref;

import kotlin.reflect.KProperty1;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

final class KotlinOptFieldReference<T, R> implements OptFieldReference<T, R> {
    private final KProperty1<T, R> property;

    KotlinOptFieldReference(KProperty1<T, R> property) {
        this.property = property;
    }

    @Override
    public Opt<R> getO(T source) {
        return Opt.ofNullable(property.get(source));
    }

    @Override
    public String name(String delimiter) {
        return property.getName();
    }

    @Override
    public String name() {
        return property.getName();
    }
}
