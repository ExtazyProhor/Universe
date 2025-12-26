package ru.prohor.universe.jocasta.morphia.impl;

import dev.morphia.transactions.MorphiaSession;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransaction;

public class MongoMorphiaTransaction implements MongoTransaction {
    private final MorphiaSession session;

    public MongoMorphiaTransaction(MorphiaSession session) {
        this.session = session;
    }

    @Override
    public <T extends MongoEntityPojo<?>> MongoRepository<T> wrap(MongoRepository<T> repository) {
        if (repository instanceof MongoMorphiaRepository<T> morphia)
            return new MongoMorphiaRepository<>(morphia.repository.copy(session), repository.type());
        throw new IllegalArgumentException("MongoMorphiaTransaction must consume MongoMorphiaRepository");
    }
}
