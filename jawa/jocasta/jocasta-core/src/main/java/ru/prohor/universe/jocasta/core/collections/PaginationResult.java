package ru.prohor.universe.jocasta.core.collections;

import java.util.List;

public record PaginationResult<T>(
        List<T> values,
        int page,
        int lastPage
) {}
