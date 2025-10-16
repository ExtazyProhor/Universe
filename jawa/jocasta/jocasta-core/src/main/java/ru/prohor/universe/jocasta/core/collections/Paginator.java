package ru.prohor.universe.jocasta.core.collections;

import java.util.List;
import java.util.stream.Stream;

public class Paginator { // TODO move to custom List
    private Paginator() {}

    public static <T> List<T> paginate(List<T> list, long page, int size) {
        check(page, size);
        return unsafePaginate(list, page, size);
    }

    public static <T> List<T> paginateOrLastPage(List<T> list, long page, int size) {
        check(page, size);
        if (list.isEmpty())
            return list;
        int length = list.size();
        long skip = page * size;
        if (skip < length)
            return unsafePaginate(list, page, size);
        return unsafePaginate(list, (length - 1) / size, size);
    }

    private static <T> List<T> unsafePaginate(List<T> list, long page, int size) {
        return unsafePaginate(list.stream(), page, size).toList();
    }

    private static <T> Stream<T> unsafePaginate(Stream<T> stream, long page, int size) {
        return stream.skip(page * size).limit(size);
    }

    private static void check(long page, int size) {
        if (page < 0)
            throw new IllegalArgumentException("page must be greater than or equal 0");
        if (size < 1)
            throw new IllegalArgumentException("page size must be greater than or equal 1");
    }
}
