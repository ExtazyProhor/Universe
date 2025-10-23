package ru.prohor.universe.jocasta.morphia;

public class MongoInMemoryTransaction implements MongoTransaction {
    @Override
    public <T> MongoRepository<T> wrap(MongoRepository<T> repository) {
        if (repository instanceof MongoInMemoryRepository<T>)
            return repository;
        throw new IllegalArgumentException("MongoInMemoryTransaction must consume MongoInMemoryRepository");
    }
}
