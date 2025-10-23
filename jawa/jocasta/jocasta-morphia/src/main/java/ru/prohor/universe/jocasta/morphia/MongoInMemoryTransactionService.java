package ru.prohor.universe.jocasta.morphia;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;

public class MongoInMemoryTransactionService implements MongoTransactionService {
    @Override
    public synchronized <E> E withTransaction(MonoFunction<MongoTransaction, E> transaction) {
        return transaction.apply(new MongoInMemoryTransaction());
    }
}
