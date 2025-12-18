package ru.prohor.universe.bobafett.configuration;

import dev.morphia.Datastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.bobafett.data.dto.BobaFettUserDto;
import ru.prohor.universe.bobafett.data.dto.CustomHolidayDto;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInstanceConfiguration;
import ru.prohor.universe.jocasta.morphia.impl.MongoMorphiaRepository;
import ru.prohor.universe.jocasta.morphia.MongoRepository;

@Configuration
@Profile("stable | canary")
@Import(MongoInstanceConfiguration.class)
public class BobaFettMongoConfiguration {
    @Bean
    public MongoRepository<BobaFettUser> bobaFettUserRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(datastore, BobaFettUserDto.class, BobaFettUser::fromDto);
    }

    @Bean
    public MongoRepository<CustomHoliday> customHolidayRepository(Datastore datastore) {
        return MongoMorphiaRepository.createRepository(datastore, CustomHolidayDto.class, CustomHoliday::fromDto);
    }
}
