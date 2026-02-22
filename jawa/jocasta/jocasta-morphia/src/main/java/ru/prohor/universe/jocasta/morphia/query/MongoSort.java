package ru.prohor.universe.jocasta.morphia.query;

import dev.morphia.query.Sort;

import java.util.Comparator;

public interface MongoSort<T> {
    Comparator<T> inMemory();

    Sort[] morphia();
}
