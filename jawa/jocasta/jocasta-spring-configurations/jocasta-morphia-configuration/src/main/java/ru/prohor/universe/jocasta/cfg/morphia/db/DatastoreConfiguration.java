package ru.prohor.universe.jocasta.cfg.morphia.db;

import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatastoreConfiguration {
    @Bean
    public Datastore datastore(
            @Value("${universe.jocasta.mongo.database}") String database,
            MongoClient mongoClient
    ) {
        return Morphia.createDatastore(mongoClient, database);
    }
}
