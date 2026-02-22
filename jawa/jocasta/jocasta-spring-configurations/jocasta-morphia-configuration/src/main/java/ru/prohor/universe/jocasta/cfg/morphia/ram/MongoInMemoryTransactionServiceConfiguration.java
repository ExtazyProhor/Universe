package ru.prohor.universe.jocasta.cfg.morphia.ram;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryTransactionService;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;

@Configuration
public class MongoInMemoryTransactionServiceConfiguration {
    @Bean
    public MongoTransactionService transactionService() {
        return new MongoInMemoryTransactionService();
    }
}
