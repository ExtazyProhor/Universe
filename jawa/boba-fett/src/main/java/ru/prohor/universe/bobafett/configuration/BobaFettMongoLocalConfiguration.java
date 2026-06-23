package ru.prohor.universe.bobafett.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.cfg.morphia.MongoInMemoryConfiguration;
import ru.prohor.universe.jocasta.jackson.morphia.MongoFileRepository;
import ru.prohor.universe.jocasta.morphia.MongoRepository;

@Configuration
@Profile("local | testing")
@Import(MongoInMemoryConfiguration.class)
public class BobaFettMongoLocalConfiguration {
    @Bean
    public MongoRepository<BobaFettUser> bobaFettUserRepository(
            @Value("${universe.boba-fett.collection-file.users}") String usersCollectionFileName
    ) {
        return new MongoFileRepository<>(BobaFettUser::id, BobaFettUser.class, usersCollectionFileName);
    }

    @Bean
    public MongoRepository<CustomHoliday> customHolidayRepository(
            @Value("${universe.boba-fett.collection-file.custom-holidays}") String customHolidaysCollectionFileName
    ) {
        return new MongoFileRepository<>(CustomHoliday::id, CustomHoliday.class, customHolidaysCollectionFileName);
    }
}
