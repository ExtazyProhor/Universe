package ru.prohor.universe.jocasta.core.features.fieldref;

import kotlin.reflect.KProperty1;

final class KotlinFieldReference<T, R> implements FieldReference<T, R> {
    private final KProperty1<T, R> property;

    KotlinFieldReference(KProperty1<T, R> property) {
        this.property = property;
    }

    @Override
    public R get(T source) {
        return property.get(source);
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
