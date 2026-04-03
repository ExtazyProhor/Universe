package ru.prohor.universe.padawan.spring.polymorphia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInMemoryConfiguration;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryRepository;
import ru.prohor.universe.padawan.spring.polymorphia.pojo.Container;

@Configuration
@Profile("local | testing")
@Import(MongoInMemoryConfiguration.class)
public class PadawanMongoLocalConfiguration {
    @Bean
    public MongoRepository<Container> containerRepository() {
        return new MongoInMemoryRepository<>(Container::id, Container.class);
    }
}
