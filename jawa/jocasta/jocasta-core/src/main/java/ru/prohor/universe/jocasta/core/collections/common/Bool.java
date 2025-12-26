package ru.prohor.universe.jocasta.core.collections.common;

public interface Bool {
    boolean unwrap();

    static Bool of(boolean value) {
        return value ? TRUE : FALSE;
    }

    default int asInt() {
        return unwrap() ? 1 : 0;
    }

    Bool TRUE = () -> true;

    Bool FALSE = () -> false;
}
