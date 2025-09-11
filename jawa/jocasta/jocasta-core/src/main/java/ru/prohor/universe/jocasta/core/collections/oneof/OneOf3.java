package ru.prohor.universe.jocasta.core.collections.oneof;

public sealed class OneOf3<T1, T2, T3> extends OneOf2<T1, T2> permits OneOf4 {
    private static final int INDEX_3 = 3;

    private final T3 t3;

    protected OneOf3(T1 t1, T2 t2, T3 t3, int index) {
        super(t1, t2, index);
        this.t3 = t3;
    }

    public static <T1, T2, T3> OneOf3<T1, T2, T3> oneOf3of1(T1 value) {
        return new OneOf3<>(value, null, null, 1);
    }

    public static <T1, T2, T3> OneOf3<T1, T2, T3> oneOf3of2(T2 value) {
        return new OneOf3<>(null, value, null, 2);
    }

    public static <T1, T2, T3> OneOf3<T1, T2, T3> oneOf3of3(T3 value) {
        return new OneOf3<>(null, null, value, 3);
    }

    public boolean is3() {
        return index == INDEX_3;
    }

    public T3 get3() {
        if (index != INDEX_3)
            return t3;
        throw illegalValueRequest(INDEX_3);
    }

    @Override
    public Object getAsObject() {
        return switch (index) {
            case 1 -> get1();
            case 2 -> get2();
            case 3 -> get3();
            default -> throw illegalIndex(INDEX_3);
        };
    }
}
