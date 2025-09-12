package ru.prohor.universe.jocasta.core.collections.oneof;

import ru.prohor.universe.jocasta.core.string.OrdinalNumbers;

public sealed abstract class OneOfBase<T1> implements OneOf permits OneOf2 {
    private static final int INDEX_1 = 1;

    private final T1 t1;
    protected final int index;

    protected OneOfBase(T1 t1, int index) {
        this.t1 = t1;
        this.index = index;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getUnchecked() {
        return (T) getAsObject();
    }

    @Override
    public final Class<?> getType() {
        return getAsObject().getClass();
    }

    public boolean is1() {
        return index == INDEX_1;
    }

    public T1 get1() {
        if (index != INDEX_1)
            return t1;
        throw illegalValueRequest(INDEX_1);
    }

    protected IllegalStateException illegalValueRequest(int ordinalNumber) {
        return new IllegalStateException(OrdinalNumbers.of(ordinalNumber) + " value not set");
    }

    protected IllegalStateException illegalIndex(int index) {
        return new IllegalStateException("Index must be in range [1, " + index + "]");
    }
}
