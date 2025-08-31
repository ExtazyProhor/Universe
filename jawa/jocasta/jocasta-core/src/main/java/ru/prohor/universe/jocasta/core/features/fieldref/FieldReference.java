package ru.prohor.universe.jocasta.core.features.fieldref;

import ru.prohor.universe.jocasta.core.collections.Opt;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;

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
public interface FieldReference<T> extends Serializable {
    @SuppressWarnings("unused")
    Object get(T source);

    default String name() {
        try {
            Method write = this.getClass().getDeclaredMethod("writeReplace");
            write.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) write.invoke(this);

            Class<?> type = Class.forName(lambda.getImplClass().replace('/', '.'));
            if (!type.isRecord())
                throw new IllegalArgumentException("Field reference must point to the record method");

            String getter = lambda.getImplMethodName();
            if (!isRecordComponent(type, getter))
                throw new IllegalArgumentException("Field reference must be a record component");

            return Opt.ofNullable(type.getDeclaredField(getter).getAnnotation(Name.class))
                    .map(Name::value)
                    .filter(value -> !value.isEmpty())
                    .orElse(getter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error when calculating field name", e);
        }
    }

    private static boolean isRecordComponent(Class<?> type, String name) {
        return Arrays.stream(type.getRecordComponents())
                .map(component -> component.getAccessor().getName())
                .anyMatch(accessor -> accessor.equals(name));
    }
}
