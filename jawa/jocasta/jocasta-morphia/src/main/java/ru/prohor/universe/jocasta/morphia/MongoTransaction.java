package ru.prohor.universe.jocasta.morphia;

public interface MongoTransaction {
    <T extends MongoEntityPojo<?>> MongoRepository<T> wrap(MongoRepository<T> repository);
}
