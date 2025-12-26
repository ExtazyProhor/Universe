package ru.prohor.universe.jocasta.cfg.morphia;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.cfg.morphia.db.DatastoreConfiguration;
import ru.prohor.universe.jocasta.cfg.morphia.db.MongoClientConfiguration;
import ru.prohor.universe.jocasta.cfg.morphia.db.MongoTransactionServiceConfiguration;


@Configuration
@Import({
        MongoClientConfiguration.class,
        DatastoreConfiguration.class,
        MongoTransactionServiceConfiguration.class,
        MongoSharedConfiguration.class,
})
public class MongoInstanceConfiguration {}
