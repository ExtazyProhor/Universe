package ru.prohor.universe.jocasta.core.features.fieldref;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.AnnotatedElement;
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
            SerializedLambda serializedLambda = (SerializedLambda) write.invoke(this);

            Class<?> type = Class.forName(serializedLambda.getImplClass().replace('/', '.'));
            String getter = serializedLambda.getImplMethodName();
            AnnotatedElement annotated = type.isRecord()
                    ? extractForRecord(type, getter)
                    : extractForClassOrInterface(type, getter);

            return Opt.ofNullable(annotated.getAnnotation(Name.class))
                    .map(Name::value)
                    .filter(value -> !value.isEmpty())
                    .orElse(getter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error when calculating field name", e);
        }
    }

    private AnnotatedElement extractForRecord(Class<?> type, String getter) throws NoSuchFieldException {
        if (isNotRecordComponent(type, getter)) {
            throw new IllegalArgumentException("Field reference must be a record component, if it used on a record");
        }
        return type.getDeclaredField(getter);
    }

    private AnnotatedElement extractForClassOrInterface(Class<?> type, String getter) {
        for (Method method : type.getDeclaredMethods()) {
            if (method.getName().equals(getter)) {
                return method;
            }
        }
        throw new IllegalArgumentException("no such method: " + getter);
    }

    private static boolean isNotRecordComponent(Class<?> type, String name) {
        return Arrays.stream(type.getRecordComponents())
                .map(component -> component.getAccessor().getName())
                .noneMatch(accessor -> accessor.equals(name));
    }
}
