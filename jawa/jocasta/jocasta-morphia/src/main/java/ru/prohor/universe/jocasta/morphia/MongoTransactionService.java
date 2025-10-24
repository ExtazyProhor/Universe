package ru.prohor.universe.jocasta.morphia;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;

public interface MongoTransactionService {
    <T> MongoTransactionResult<T> withTransaction(MonoFunction<MongoTransaction, T> transaction);
}
