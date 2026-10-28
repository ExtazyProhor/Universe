package ru.prohor.universe.jocasta.core.features.fieldref;

import kotlin.reflect.KProperty1;

/**
 * Kotlin Field Reference static methods
 */
public class KFR {
    public static <T, R> FieldReference<T, R> wrap(KProperty1<T, R> property) {
        return new KotlinFieldReference<>(property);
    }

    public static <T, R> OptFieldReference<T, R> wrapO(KProperty1<T, R> property) {
        return new KotlinOptFieldReference<>(property);
    }

    public static <T, R> FieldReferenceChain<T, R> chain(KProperty1<T, R> property) {
        return FieldReferenceChain.create(wrap(property));
    }

    public static <T, R> FieldReferenceChain<T, R> chainO(KProperty1<T, R> property) {
        return FieldReferenceChain.create(wrapO(property));
    }
}
