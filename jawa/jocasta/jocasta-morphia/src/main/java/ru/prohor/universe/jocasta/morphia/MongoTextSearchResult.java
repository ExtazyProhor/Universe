package ru.prohor.universe.jocasta.morphia;

import java.util.List;

public record MongoTextSearchResult<T> (
        List<T> entities,
        long total,
        int page,
        int lastPage
) {}
