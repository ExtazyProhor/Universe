package ru.prohor.universe.jocasta.cfg.morphia.db;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class MongoClientConfiguration {
    @Bean
    public MongoClient mongoClient(
            @Value("${universe.jocasta.mongo.min-connections:#{2}}") int minConnections,
            @Value("${universe.jocasta.mongo.max-connections:#{15}}") int maxConnections,
            @Value("${universe.jocasta.mongo.max-wait-time:#{100}}") int maxWaitTime,
            @Value("${universe.jocasta.mongo.connect-timeout:#{1000}}") int connectTimeout,
            @Value("${universe.jocasta.mongo.read-timeout:#{2000}}") int readTimeout,
            @Value("${universe.jocasta.mongo.host}") String host,
            @Value("${universe.jocasta.mongo.port:#{27017}}") int port,
            @Value("${universe.jocasta.mongo.user}") String user,
            @Value("${universe.jocasta.mongo.password}") String password,
            @Value("${universe.jocasta.mongo.database}") String database
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
                        List.of(new ServerAddress(host, port))
                ))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .credential(MongoCredential.createCredential(
                        user,
                        database,
                        password.toCharArray()
                )).build()
        );
    }
}
