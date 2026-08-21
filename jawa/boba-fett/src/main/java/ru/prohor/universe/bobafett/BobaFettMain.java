package ru.prohor.universe.bobafett;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencySubscriptionOptions;
import ru.prohor.universe.bobafett.feature.currency.CurrencyService;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.jackson.JacksonJocastaCoreConfiguration;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.jackson.JacksonMorphiaConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.JocastaAutoConfiguration;

@Configuration
@ComponentScan
@EnableScheduling
@Import({
        JocastaAutoConfiguration.class,
        HolocronConfiguration.class,
        JacksonMorphiaConfiguration.class,
        JacksonJocastaCoreConfiguration.class,
})
public class BobaFettMain {
    static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BobaFettMain.class, args);
        // TODO
        MongoRepository<BobaFettUser> repository = context.getBean("bobaFettUserRepository", MongoRepository.class);
        CurrencyService currencyService = context.getBean(CurrencyService.class);
        repository.safeUpdateAll(user -> {
            CurrencySubscriptionOptions options = user.currencySubscriptionOptions().map(
                    o -> currencyService.createOptions(
                            Opt.of(o.dailyDistributionTime()),
                            Opt.of(o.subscriptionIsActive()),
                            o.selectedCurrencies()
                    )
            ).orElse(currencyService.createOptions(Opt.empty(), Opt.empty(), Opt.empty()));
            return user.toBuilder().currencySubscriptionOptions(Opt.of(options)).build();
        });
    }
}
