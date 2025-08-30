package ru.prohor.universe.yahtzee.configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfiguration {
    @Bean
    public MongoClient mongoClient(
            @Value("${universe.yahtzee.mongo.min-connections}") int minConnections,
            @Value("${universe.yahtzee.mongo.max-connections}") int maxConnections,
            @Value("${universe.yahtzee.mongo.max-wait-time}") int maxWaitTime,
            @Value("${universe.yahtzee.mongo.connect-timeout}") int connectTimeout,
            @Value("${universe.yahtzee.mongo.read-timeout}") int readTimeout,
            @Value("${universe.yahtzee.mongo.host}") String host,
            @Value("${universe.yahtzee.mongo.post}") int post,
            @Value("${universe.yahtzee.mongo.user}") String user,
            @Value("${universe.yahtzee.mongo.password}") String password,
            @Value("${universe.yahtzee.mongo.database}") String database
    ) {
        return MongoClients.create(MongoClientSettings.builder()
                .applyToSocketSettings(builder -> {
                    builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
                    builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
                })
                .applyToConnectionPoolSettings(builder -> {
                    builder.maxWaitTime(maxWaitTime, TimeUnit.MILLISECONDS);
                    builder.minSize(minConnections);
                    builder.maxSize(maxConnections);
                })
                .applyToClusterSettings(builder -> builder.hosts(
                        List.of(new ServerAddress(host, post))
                ))
                .credential(MongoCredential.createCredential(
                        user,
                        database,
                        password.toCharArray()
                )).build()
        );
    }

    @Bean
    @SuppressWarnings("all")
    public Datastore datastore(
            @Value("${universe.yahtzee.mongo.database}") String database,
            MongoClient mongoClient
    ) {
        // TODO MapperOptions.builder()
        Datastore datastore = Morphia.createDatastore(mongoClient, database);
        datastore.getMapper().mapPackage("ru.prohor.universe.yahtzee.data.entities.dto");
        return datastore;
    }
}
