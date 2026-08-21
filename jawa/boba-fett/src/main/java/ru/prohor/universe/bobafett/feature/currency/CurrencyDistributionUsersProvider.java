package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencySubscriptionOptions;
import ru.prohor.universe.bobafett.data.pojo.DistributionTime;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldProperties;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;

import java.util.List;

@Service
public class CurrencyDistributionUsersProvider {
    private static final FieldProperties<BobaFettUser, ?> CURRENCY_SUBSCRIPTION_OPTIONS_KEY = FR
            .wrap(BobaFettUser::currencySubscriptionOptions);
    private static final FieldProperties<BobaFettUser, Integer> HOUR_KEY = FR
            .chain(BobaFettUser::currencySubscriptionOptions)
            .then(CurrencySubscriptionOptions::dailyDistributionTime)
            .then(DistributionTime::hour);
    private static final FieldProperties<BobaFettUser, Integer> MINUTE_KEY = FR
            .chain(BobaFettUser::currencySubscriptionOptions)
            .then(CurrencySubscriptionOptions::dailyDistributionTime)
            .then(DistributionTime::minute);
    private static final FieldProperties<BobaFettUser, Boolean> IS_ACTIVE_KEY = FR
            .chain(BobaFettUser::currencySubscriptionOptions)
            .then(CurrencySubscriptionOptions::subscriptionIsActive);

    public List<BobaFettUser> findUsersToDistribution(
            MongoRepository<BobaFettUser> repository,
            int hour,
            int minute
    ) {
        MongoFilter<BobaFettUser> filter = MongoFilters.and(
                MongoFilters.exists(CURRENCY_SUBSCRIPTION_OPTIONS_KEY),
                MongoFilters.eq(HOUR_KEY, hour),
                MongoFilters.eq(MINUTE_KEY, minute),
                MongoFilters.eq(IS_ACTIVE_KEY, true)
        );
        return repository.find(filter);
    }
}
