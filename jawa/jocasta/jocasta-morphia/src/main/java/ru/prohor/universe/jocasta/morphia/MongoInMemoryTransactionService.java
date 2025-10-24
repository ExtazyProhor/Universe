package ru.prohor.universe.jocasta.morphia;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;

public class MongoInMemoryTransactionService implements MongoTransactionService {
    @Override
    public synchronized <T> MongoTransactionResult<T> withTransaction(MonoFunction<MongoTransaction, T> transaction) {
        return MongoTransactionResult.success(transaction.apply(new MongoInMemoryTransaction()));
    }
}
