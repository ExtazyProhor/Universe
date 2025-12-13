package ru.prohor.universe.jocasta.morphia;

import dev.morphia.Datastore;
import ru.prohor.universe.jocasta.core.functional.MonoConsumer;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

public class MongoMorphiaTransactionService implements MongoTransactionService {
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
}
