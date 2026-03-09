package ru.prohor.universe.jocasta.core.utils;

import java.util.List;

public class CastUtils {
    private CastUtils() {}

    @SuppressWarnings("unchecked")
    public static <T1, T2> List<T2> cast(List<T1> list) {
        return list.stream()
                .map(container -> (T2) container)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2> T2 cast(T1 t1) {
        return (T2) t1;
    }
}
