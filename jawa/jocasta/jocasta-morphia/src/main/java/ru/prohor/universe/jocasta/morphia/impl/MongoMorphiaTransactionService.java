package ru.prohor.universe.jocasta.morphia.impl;

import com.mongodb.ClientSessionOptions;
import com.mongodb.ReadConcern;
import com.mongodb.TransactionOptions;
import dev.morphia.Datastore;
import dev.morphia.transactions.MorphiaSession;
import ru.prohor.universe.jocasta.core.functional.MonoConsumer;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.morphia.MongoTransaction;
import ru.prohor.universe.jocasta.morphia.MongoTransactionResult;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;

public class MongoMorphiaTransactionService implements MongoTransactionService {
    private final ClientSessionOptions causallyConsistentClientSessionOptions = ClientSessionOptions.builder()
            .causallyConsistent(true)
            .build();
    private final TransactionOptions snaphotTransactionOptions = TransactionOptions.builder()
            .readConcern(ReadConcern.SNAPSHOT)
            .build();
    private final Datastore datastore;

    public MongoMorphiaTransactionService(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public <T> MongoTransactionResult<T> withTransaction(MonoFunction<MongoTransaction, T> transaction) {
        try {
            return MongoTransactionResult.success(
                    datastore.withTransaction(
                            session -> transaction.apply(new MongoMorphiaTransaction(session))
                    )
            );
        } catch (Exception e) {
            // TODO log
            e.printStackTrace();
            return MongoTransactionResult.error();
        }
    }

    @Override
    public boolean withTransaction(MonoConsumer<MongoTransaction> transaction) {
        try {
            datastore.withTransaction(
                    session -> {
                        transaction.accept(new MongoMorphiaTransaction(session));
                        return Boolean.TRUE;
                    }
            );
            return true;
        } catch (Exception e) {
            // TODO log
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public <T> T withReadSnapshot(MonoFunction<MongoTransaction, T> transaction) {
        try (MorphiaSession session = datastore.startSession(causallyConsistentClientSessionOptions)) {
            session.startTransaction(snaphotTransactionOptions);
            T result = transaction.apply(new MongoMorphiaTransaction(session));
            session.commitTransaction();
            return result;
        }
    }

    @Override
    public void withReadSnapshot(MonoConsumer<MongoTransaction> transaction) {
        try (MorphiaSession session = datastore.startSession(causallyConsistentClientSessionOptions)) {
            session.startTransaction(snaphotTransactionOptions);
            transaction.accept(new MongoMorphiaTransaction(session));
            session.commitTransaction();
        }
    }
}
