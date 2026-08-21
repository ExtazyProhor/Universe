package ru.prohor.universe.jocasta.morphia.configuration;

import dev.morphia.Datastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.morphia.impl.MongoMorphiaTransactionService;

@Configuration
@Import(DatastoreConfiguration.class)
public class MongoInstanceConfiguration {
    @Bean
    public MongoTransactionService transactionService(Datastore datastore) {
        return new MongoMorphiaTransactionService(datastore);
    }
}
