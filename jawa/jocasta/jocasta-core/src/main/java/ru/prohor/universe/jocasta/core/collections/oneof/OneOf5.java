package ru.prohor.universe.jocasta.core.collections.oneof;

public non-sealed class OneOf5<T1, T2, T3, T4, T5> extends OneOf4<T1, T2, T3, T4> {
    private static final int INDEX_5 = 5;

    private final T5 t5;

    protected OneOf5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, int index) {
        super(t1, t2, t3, t4, index);
        this.t5 = t5;
    }

    public static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of1(T1 value) {
        return new OneOf5<>(value, null, null, null, null, 1);
    }

    public static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of2(T2 value) {
        return new OneOf5<>(null, value, null, null, null, 2);
    }

    public static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of3(T3 value) {
        return new OneOf5<>(null, null, value, null, null, 3);
    }

    public static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of4(T4 value) {
        return new OneOf5<>(null, null, null, value, null, 4);
    }

    public static <T1, T2, T3, T4, T5> OneOf5<T1, T2, T3, T4, T5> oneOf5of5(T5 value) {
        return new OneOf5<>(null, null, null, null, value, 5);
    }

    public boolean is5() {
        return index == INDEX_5;
    }

    public T5 get5() {
        if (index != INDEX_5)
            return t5;
        throw illegalValueRequest(INDEX_5);
    }

    @Override
    public Object getAsObject() {
        return switch (index) {
            case 1 -> get1();
            case 2 -> get2();
            case 3 -> get3();
            case 4 -> get4();
            case 5 -> get5();
            default -> throw illegalIndex(INDEX_5);
        };
    }
}
