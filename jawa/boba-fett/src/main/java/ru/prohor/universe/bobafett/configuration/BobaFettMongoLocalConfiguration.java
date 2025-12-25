package ru.prohor.universe.bobafett.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInMemoryConfiguration;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryRepository;

@Configuration
@Profile("local | testing")
@Import(MongoInMemoryConfiguration.class)
public class BobaFettMongoLocalConfiguration {
    @Bean
    public MongoRepository<BobaFettUser> bobaFettUserRepository() {
        return new MongoInMemoryRepository<>(BobaFettUser::id);
    }

    @Bean
    public MongoRepository<CustomHoliday> customHolidayRepository() {
        return new MongoInMemoryRepository<>(CustomHoliday::id);
    }
}
