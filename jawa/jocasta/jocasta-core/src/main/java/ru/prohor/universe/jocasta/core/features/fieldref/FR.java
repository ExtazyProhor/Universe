package ru.prohor.universe.jocasta.core.features.fieldref;

/**
 * Field Reference static methods
 */
public class FR {
    public static <T extends Record, R> FieldReference<T, R> wrap(FieldReference<T, R> fieldRef) {
        return fieldRef;
    }

    public static <T extends Record, R> OptFieldReference<T, R> wrapO(OptFieldReference<T, R> optFieldRef) {
        return optFieldRef;
    }

    public static <T extends Record, R> FieldReferenceChain<T, R> chain(FieldReference<T, R> fieldRef) {
        return FieldReferenceChain.create(fieldRef);
    }

    public static <T extends Record, R> FieldReferenceChain<T, R> chainO(OptFieldReference<T, R> optFieldRef) {
        return FieldReferenceChain.create(optFieldRef);
    }
}
