package ru.prohor.universe.jocasta.core.features.fieldref;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;

public sealed interface FieldRef<T, R>
        extends Serializable, FieldProperties<T, R>
        permits FieldReference, OptFieldReference {

    @Override
    default String name(String delimiter) {
        return extractFieldName();
    }

    @Override
    default String name() {
        return extractFieldName();
    }

    private String extractFieldName() {
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
