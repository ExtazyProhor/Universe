package ru.prohor.universe.jocasta.cfg.morphia.db;

import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.config.MorphiaConfig;
import org.bson.codecs.Codec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.morphia.MongoCodecProvider;

import java.util.List;

@Configuration
public class DatastoreConfiguration {
    @Bean
    public MongoCodecProvider mongoCodecProvider(List<Codec<?>> codecs) {
        return new MongoCodecProvider(codecs);
    }

    @Bean
    public Datastore datastore(
            @Value("${universe.jocasta.mongo.database}") String database,
            MongoClient mongoClient,
            MongoCodecProvider mongoCodecProvider
    ) {
        MorphiaConfig config = MorphiaConfig.load()
                .codecProvider(mongoCodecProvider)
                .database(database);
        return Morphia.createDatastore(
                mongoClient,
                config
        );
    }
}
