package ru.prohor.universe.padawan.tests.polymorphia;

import dev.morphia.Datastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInstanceConfiguration;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.impl.MongoMorphiaRepository;
import ru.prohor.universe.padawan.tests.polymorphia.dto.ContainerDto;
import ru.prohor.universe.padawan.tests.polymorphia.pojo.Container;

@Configuration
@Profile("stable | canary")
@Import(MongoInstanceConfiguration.class)
public class PadawanMongoStableConfiguration {
    @Bean
    public MongoRepository<Container> containerRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(
                datastore,
                Container.class,
                ContainerDto.class,
                Container::fromDto
        );
    }
}
