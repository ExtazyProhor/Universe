package ru.prohor.universe.jocasta.morphia.impl;

import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransaction;

public class MongoInMemoryTransaction implements MongoTransaction {
    @Override
    public <T> MongoRepository<T> wrap(MongoRepository<T> repository) {
        if (repository instanceof MongoInMemoryRepository<T>)
            return repository;
        throw new IllegalArgumentException("MongoInMemoryTransaction must consume MongoInMemoryRepository");
    }
}
