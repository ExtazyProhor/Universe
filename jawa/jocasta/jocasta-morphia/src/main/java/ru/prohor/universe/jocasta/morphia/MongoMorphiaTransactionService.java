package ru.prohor.universe.jocasta.morphia;

import dev.morphia.Datastore;
import ru.prohor.universe.jocasta.core.collections.common.Range;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

public class MongoMorphiaTransactionService implements MongoTransactionService {
    private final Datastore datastore;
    private final int retries;

    public MongoMorphiaTransactionService(Datastore datastore, int retries) {
        this.datastore = datastore;
        this.retries = retries;
    }

    @Override
    public <T> MongoTransactionResult<T> withTransaction(MonoFunction<MongoTransaction, T> transaction) {
        for (int ignored : Range.range(retries))
            try {
                return MongoTransactionResult.success(
                        datastore.withTransaction(
                                session -> transaction.apply(new MongoMorphiaTransaction(session))
                        )
                );
            } catch (Exception e) {
                // TODO log
                e.printStackTrace();
            }
        return MongoTransactionResult.error();
    }
}
