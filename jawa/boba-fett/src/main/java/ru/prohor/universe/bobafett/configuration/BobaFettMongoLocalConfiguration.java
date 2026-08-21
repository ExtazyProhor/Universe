package ru.prohor.universe.bobafett.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencyRate;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.jackson.morphia.MongoFileRepository;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.configuration.MongoInMemoryConfiguration;

@Configuration
// TODO @Profile("local | testing")
@Import(MongoInMemoryConfiguration.class)
public class BobaFettMongoLocalConfiguration {
    @Bean
    public MongoRepository<BobaFettUser> bobaFettUserRepository(
            ObjectMapper objectMapper,
            @Value("${universe.boba-fett.collection-file.users}") String usersCollectionFileName
    ) {
        return new MongoFileRepository<>(
                BobaFettUser::id,
                BobaFettUser.class,
                usersCollectionFileName,
                objectMapper
        );
    }

    @Bean
    public MongoRepository<CustomHoliday> customHolidayRepository(
            ObjectMapper objectMapper,
            @Value("${universe.boba-fett.collection-file.custom-holidays}") String customHolidaysCollectionFileName
    ) {
        return new MongoFileRepository<>(
                CustomHoliday::id,
                CustomHoliday.class,
                customHolidaysCollectionFileName,
                objectMapper
        );
    }

    @Bean
    public MongoRepository<CurrencyRate> currencyRatesRepository(
            ObjectMapper objectMapper,
            @Value("${universe.boba-fett.collection-file.currency-rates}") String currencyRatesCollectionFileName
    ) {
        return new MongoFileRepository<>(
                CurrencyRate::id,
                CurrencyRate.class,
                currencyRatesCollectionFileName,
                objectMapper
        );
    }
}
