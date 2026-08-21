package ru.prohor.universe.jocasta.morphia.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryTransactionService;

@Configuration
public class MongoInMemoryConfiguration {
    @Bean
    public MongoTransactionService transactionService() {
        return new MongoInMemoryTransactionService();
    }
}
