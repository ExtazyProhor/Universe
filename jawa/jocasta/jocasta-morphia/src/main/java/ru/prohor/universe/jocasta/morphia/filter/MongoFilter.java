package ru.prohor.universe.jocasta.morphia.filter;

import dev.morphia.query.filters.Filter;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

public interface MongoFilter<T> {
    MonoPredicate<T> inMemory();

    Filter morphia();
}
