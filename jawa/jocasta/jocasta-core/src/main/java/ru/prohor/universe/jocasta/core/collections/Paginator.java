package ru.prohor.universe.jocasta.core.collections;

import java.util.List;

public class Paginator { // TODO move to custom List
    private Paginator() {}

    /**
     * Возвращает элементы указанной страницы.
     *
     * <p>Нумерация страниц начинается с нуля. Если запрошенная страница
     * выходит за пределы списка, возвращается пустой список.</p>
     *
     * @param list список, из которого извлекается страница
     * @param page номер страницы, начиная с {@code 0}
     * @param size максимальное количество элементов на странице
     * @param <T> тип элементов списка
     * @return элементы запрошенной страницы
     * @throws IllegalArgumentException если {@code page} отрицателен
     *                                  или {@code size} меньше 1
     */
    public static <T> List<T> paginate(List<T> list, long page, int size) {
        check(page, size);
        return unsafePaginate(list, page, size);
    }

    /**
     * Возвращает указанную страницу или последнюю страницу, если запрошенная
     * страница выходит за пределы списка.
     *
     * <p>Если список пуст, возвращается пустой список.</p>
     *
     * @param list список, из которого извлекается страница
     * @param page номер страницы, начиная с {@code 0}
     * @param size максимальное количество элементов на странице
     * @param <T> тип элементов списка
     * @return элементы запрошенной страницы либо последней страницы,
     *         если запрошенная страница недоступна
     * @throws IllegalArgumentException если {@code page} отрицателен
     *                                  или {@code size} меньше 1
     */
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

    /**
     * Возвращает указанную страницу вместе с информацией о текущей
     * и последней доступной странице.
     *
     * <p>Нумерация страниц начинается с нуля. Если список пуст,
     * возвращается результат с текущей и последней страницей, равными {@code 0}.</p>
     *
     * @param list список, из которого извлекается страница
     * @param page номер страницы, начиная с {@code 0}
     * @param size максимальное количество элементов на странице
     * @param <T> тип элементов списка
     * @return результат пагинации с элементами страницы, номером текущей страницы
     *         и номером последней страницы
     * @throws IllegalArgumentException если {@code page} отрицателен
     *                                  или {@code size} меньше 1
     */
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

    /**
     * Возвращает указанную страницу или последнюю страницу, если запрошенная
     * страница выходит за пределы списка, вместе с информацией о пагинации.
     *
     * <p>Нумерация страниц начинается с нуля. Если список пуст,
     * возвращается результат с текущей и последней страницей, равными {@code 0}.</p>
     *
     * @param list список, из которого извлекается страница
     * @param page номер страницы, начиная с {@code 0}
     * @param size максимальное количество элементов на странице
     * @param <T> тип элементов списка
     * @return результат пагинации с элементами запрошенной страницы
     *         либо последней страницы, если запрошенная страница недоступна
     * @throws IllegalArgumentException если {@code page} отрицателен
     *                                  или {@code size} меньше 1
     */
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

    /**
     * Выполняет пагинацию без проверки входных параметров.
     *
     * @param list список, из которого извлекаются элементы
     * @param page номер страницы, начиная с {@code 0}
     * @param size максимальное количество элементов на странице
     * @param <T> тип элементов списка
     * @return элементы указанной страницы
     */
    private static <T> List<T> unsafePaginate(List<T> list, long page, int size) {
        return list.stream().skip(page * size).limit(size).toList();
    }

    /**
     * Вычисляет номер последней страницы для списка заданного размера.
     *
     * <p>Нумерация страниц начинается с нуля. Например, для списка из
     * 10 элементов и размера страницы {@code 3} последняя страница имеет номер {@code 3}.</p>
     *
     * @param length количество элементов в списке
     * @param pageSize размер страницы
     * @return номер последней страницы
     */
    private static int lastPage(int length, int pageSize) {
        return (length - 1) / pageSize;
    }

    /**
     * Проверяет корректность параметров пагинации.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @throws IllegalArgumentException если {@code page} отрицателен
     *                                  или {@code size} меньше 1
     */
    private static void check(long page, int size) {
        if (page < 0)
            throw new IllegalArgumentException("page must be greater than or equal 0");
        if (size < 1)
            throw new IllegalArgumentException("page size must be greater than or equal 1");
    }
}
