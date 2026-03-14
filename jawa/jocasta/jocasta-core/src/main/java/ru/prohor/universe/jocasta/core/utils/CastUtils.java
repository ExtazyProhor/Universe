package ru.prohor.universe.jocasta.core.utils;

import java.util.List;

public class CastUtils {
    private CastUtils() {}

    @SuppressWarnings("unchecked")
    public static <T> List<T> cast(List<?> list) {
        return list.stream()
                .map(t -> (T) t)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
    }
}
