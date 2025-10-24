package ru.prohor.universe.jocasta.core.collections;

import java.util.List;

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

    // TODO поменять для тестов
    public static <T> PaginationResult<T> richPaginate(List<T> list, long page, int size) {
        check(page, size);
        if (list.isEmpty())
            return new PaginationResult<>(list, 0, 0);
        return new PaginationResult<>(
                unsafePaginate(list, page, size),
                (int) page,
                lastPage(list.size(), size)
        );
    }

    // TODO поменять для тестов
    public static <T> PaginationResult<T> richPaginateOrLastPage(List<T> list, long page, int size) {
        check(page, size);
        if (list.isEmpty())
            return new PaginationResult<>(list, 0, 0);
        int length = list.size();
        long skip = page * size;
        int lastPage = lastPage(length, size);
        if (skip < length)
            return new PaginationResult<>(
                    unsafePaginate(list, page, size),
                    (int) page,
                    lastPage
            );
        return new PaginationResult<>(
                unsafePaginate(list, lastPage, size),
                lastPage,
                lastPage
        );
    }

    private static <T> List<T> unsafePaginate(List<T> list, long page, int size) {
        return list.stream().skip(page * size).limit(size).toList();
    }

    private static int lastPage(int length, int pageSize) {
        return (length - 1) / pageSize;
    }

    private static void check(long page, int size) {
        if (page < 0)
            throw new IllegalArgumentException("page must be greater than or equal 0");
        if (size < 1)
            throw new IllegalArgumentException("page size must be greater than or equal 1");
    }
}
