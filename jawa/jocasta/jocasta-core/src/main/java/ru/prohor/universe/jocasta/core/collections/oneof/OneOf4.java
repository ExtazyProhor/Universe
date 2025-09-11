package ru.prohor.universe.jocasta.core.collections.oneof;

public sealed class OneOf4<T1, T2, T3, T4> extends OneOf3<T1, T2, T3> permits OneOf5 {
    private static final int INDEX_4 = 4;

    private final T4 t4;

    protected OneOf4(T1 t1, T2 t2, T3 t3, T4 t4, int index) {
        super(t1, t2, t3, index);
        this.t4 = t4;
    }

    public static <T1, T2, T3, T4> OneOf4<T1, T2, T3, T4> oneOf4of1(T1 value) {
        return new OneOf4<>(value, null, null, null, 1);
    }

    public static <T1, T2, T3, T4> OneOf4<T1, T2, T3, T4> oneOf4of2(T2 value) {
        return new OneOf4<>(null, value, null, null, 2);
    }

    public static <T1, T2, T3, T4> OneOf4<T1, T2, T3, T4> oneOf4of3(T3 value) {
        return new OneOf4<>(null, null, value, null, 3);
    }

    public static <T1, T2, T3, T4> OneOf4<T1, T2, T3, T4> oneOf4of4(T4 value) {
        return new OneOf4<>(null, null, null, value, 4);
    }

    public boolean is4() {
        return index == INDEX_4;
    }

    public T4 get4() {
        if (index != INDEX_4)
            return t4;
        throw illegalValueRequest(INDEX_4);
    }

    @Override
    public Object getAsObject() {
        return switch (index) {
            case 1 -> get1();
            case 2 -> get2();
            case 3 -> get3();
            case 4 -> get4();
            default -> throw illegalIndex(INDEX_4);
        };
    }
}
