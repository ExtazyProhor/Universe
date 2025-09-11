package ru.prohor.universe.jocasta.core.collections.oneof;

public sealed class OneOf2<T1, T2> extends OneOfBase<T1> permits OneOf3 {
    private static final int INDEX_2 = 2;

    private final T2 t2;

    protected OneOf2(T1 t1, T2 t2, int index) {
        super(t1, index);
        this.t2 = t2;
    }

    public static <T1, T2> OneOf2<T1, T2> oneOf2of1(T1 value) {
        return new OneOf2<>(value, null, 1);
    }

    public static <T1, T2> OneOf2<T1, T2> oneOf2of2(T2 value) {
        return new OneOf2<>(null, value, 2);
    }

    public boolean is2() {
        return index == INDEX_2;
    }

    public T2 get2() {
        if (index != INDEX_2)
            return t2;
        throw illegalValueRequest(INDEX_2);
    }

    @Override
    public Object getAsObject() {
        return switch (index) {
            case 1 -> get1();
            case 2 -> get2();
            default -> throw illegalIndex(INDEX_2);
        };
    }
}
