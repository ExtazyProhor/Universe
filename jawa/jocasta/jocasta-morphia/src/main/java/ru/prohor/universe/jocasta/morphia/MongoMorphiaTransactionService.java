package ru.prohor.universe.jocasta.morphia;

import dev.morphia.Datastore;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

public class MongoMorphiaTransactionService implements MongoTransactionService {
    private final Datastore datastore;

    public MongoMorphiaTransactionService(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public <E> E withTransaction(MonoFunction<MongoTransaction, E> transaction) {
        return datastore.withTransaction(
                session -> transaction.apply(new MongoMorphiaTransaction(session))
        );
    }
}
