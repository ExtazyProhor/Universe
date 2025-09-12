package ru.prohor.universe.jocasta.core.collections.oneof;

public interface OneOf {
    Object getAsObject();

    <T> T getUnchecked();

    Class<?> getType();

    static <T1, T2> OneOf2<T1, T2> oneOf2of1(T1 value) {
        return new OneOf2<>(value, null, 1);
    }

    static <T1, T2> OneOf2<T1, T2> oneOf2of2(T2 value) {
        return new OneOf2<>(null, value, 2);
    }

    static <T1, T2, T3> OneOf3<T1, T2, T3> oneOf3of1(T1 value) {
        return new OneOf3<>(value, null, null, 1);
    }

    static <T1, T2, T3> OneOf3<T1, T2, T3> oneOf3of2(T2 value) {
        return new OneOf3<>(null, value, null, 2);
    }

    static <T1, T2, T3> OneOf3<T1, T2, T3> oneOf3of3(T3 value) {
        return new OneOf3<>(null, null, value, 3);
    }

    static <T1, T2, T3, T4> OneOf4<T1, T2, T3, T4> oneOf4of1(T1 value) {
        return new OneOf4<>(value, null, null, null, 1);
    }

    static <T1, T2, T3, T4> OneOf4<T1, T2, T3, T4> oneOf4of2(T2 value) {
        return new OneOf4<>(null, value, null, null, 2);
    }

    static <T1, T2, T3, T4> OneOf4<T1, T2, T3, T4> oneOf4of3(T3 value) {
        return new OneOf4<>(null, null, value, null, 3);
    }

    static <T1, T2, T3, T4> OneOf4<T1, T2, T3, T4> oneOf4of4(T4 value) {
        return new OneOf4<>(null, null, null, value, 4);
    }

    static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of1(T1 value) {
        return new OneOf5<>(value, null, null, null, null, 1);
    }

    static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of2(T2 value) {
        return new OneOf5<>(null, value, null, null, null, 2);
    }

    static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of3(T3 value) {
        return new OneOf5<>(null, null, value, null, null, 3);
    }

    static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of4(T4 value) {
        return new OneOf5<>(null, null, null, value, null, 4);
    }

    static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of5(T5 value) {
        return new OneOf5<>(null, null, null, null, value, 5);
    }
}
