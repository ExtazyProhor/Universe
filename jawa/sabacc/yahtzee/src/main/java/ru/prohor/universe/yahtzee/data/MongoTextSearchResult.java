package ru.prohor.universe.yahtzee.data;

import java.util.List;

public record MongoTextSearchResult<T> (
        List<T> entities,
        long total
) {}
