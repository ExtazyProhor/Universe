package ru.prohor.universe.jocasta.morphia.impl;

import ru.prohor.universe.jocasta.core.functional.MonoConsumer;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.morphia.MongoTransaction;
import ru.prohor.universe.jocasta.morphia.MongoTransactionResult;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;

public class MongoInMemoryTransactionService implements MongoTransactionService {
    @Override
    public synchronized <T> MongoTransactionResult<T> withTransaction(MonoFunction<MongoTransaction, T> transaction) {
        return MongoTransactionResult.success(transaction.apply(new MongoInMemoryTransaction()));
    }

    @Override
    public boolean withTransaction(MonoConsumer<MongoTransaction> transaction) {
        transaction.accept(new MongoInMemoryTransaction());
        return true;
    }
}
