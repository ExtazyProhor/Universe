package ru.prohor.universe.jocasta.cfg.morphia.db;

import dev.morphia.Datastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.morphia.MongoMorphiaTransactionService;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;

@Configuration
public class MongoTransactionServiceConfiguration {
    @Bean
    public MongoTransactionService transactionService(Datastore datastore) {
        return new MongoMorphiaTransactionService(datastore);
    }
}
