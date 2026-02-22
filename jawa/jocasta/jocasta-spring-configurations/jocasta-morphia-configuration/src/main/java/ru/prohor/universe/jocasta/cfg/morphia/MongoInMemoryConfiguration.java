package ru.prohor.universe.jocasta.cfg.morphia;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.cfg.morphia.ram.MongoInMemoryTransactionServiceConfiguration;

@Configuration
@Import({
        MongoInMemoryTransactionServiceConfiguration.class,
        MongoSharedConfiguration.class,
})
public class MongoInMemoryConfiguration {}
