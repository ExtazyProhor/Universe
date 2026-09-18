package ru.prohor.universe.scarif.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.configuration.MongoInMemoryConfiguration;
import ru.prohor.universe.jocasta.morphia.impl.MongoFileRepository;
import ru.prohor.universe.scarif.data.User;

@Configuration
@Import(MongoInMemoryConfiguration.class)
public class ScarifMongoConfiguration {
    @Bean
    public MongoRepository<User> scarifUserRepository(
            ObjectMapper objectMapper,
            @Value("${universe.scarif.collection-file.users}") String usersCollectionFileName
    ) {
        return new MongoFileRepository<>(
                User::id,
                User.class,
                usersCollectionFileName,
                objectMapper
        );
    }
}
