package ru.prohor.universe.jocasta.morphia.impl;

import dev.morphia.transactions.MorphiaSession;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransaction;

public class MongoMorphiaTransaction implements MongoTransaction {
    private final MorphiaSession session;

    public MongoMorphiaTransaction(MorphiaSession session) {
        this.session = session;
    }

    @Override
    public <T> MongoRepository<T> wrap(MongoRepository<T> repository) {
        if (repository instanceof MongoMorphiaRepository<T>)
            return new MongoMorphiaRepository<>(((MongoMorphiaRepository<T>) repository).repository.copy(session));
        throw new IllegalArgumentException("MongoMorphiaTransaction must consume MongoMorphiaRepository");
    }
}
