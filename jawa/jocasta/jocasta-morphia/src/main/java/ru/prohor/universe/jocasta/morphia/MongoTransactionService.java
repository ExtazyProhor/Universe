package ru.prohor.universe.jocasta.morphia;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;

public interface MongoTransactionService {
    <E> E withTransaction(MonoFunction<MongoTransaction, E> transaction);
}
