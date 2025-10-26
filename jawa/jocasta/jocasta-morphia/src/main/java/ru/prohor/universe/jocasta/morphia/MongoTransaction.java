package ru.prohor.universe.jocasta.morphia;

public interface MongoTransaction {
    <T> MongoRepository<T> wrap(MongoRepository<T> repository);
}
