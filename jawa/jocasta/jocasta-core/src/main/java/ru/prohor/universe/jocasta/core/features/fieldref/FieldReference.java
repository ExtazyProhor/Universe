package ru.prohor.universe.jocasta.core.features.fieldref;

/**
 * <h3>Usage</h3>
 * <pre>
 *     {@code
 *     public static <T> String name(PropertyRef<T> ref) {
 *         return ref.name();
 *     }
 *     }
 * </pre>
 */
@FunctionalInterface
public interface FieldReference<T> extends TypedFieldReference<T, Object> {
    static <T> String name(FieldReference<T> fieldRef) {
        return fieldRef.name();
    }
}
