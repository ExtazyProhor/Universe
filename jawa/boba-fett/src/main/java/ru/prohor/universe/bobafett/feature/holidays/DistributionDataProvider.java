package ru.prohor.universe.bobafett.feature.holidays;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.data.pojo.DistributionTime;
import ru.prohor.universe.bobafett.data.pojo.HolidaysSubscriptionOptions;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldProperties;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class DistributionDataProvider {
    private static final FieldProperties<BobaFettUser, ?> HOLIDAYS_SUBSCRIPTION_OPTIONS_KEY = FR
            .wrap(BobaFettUser::holidaysSubscriptionOptions);
    private static final FieldProperties<BobaFettUser, Integer> HOUR_KEY = FR
            .chainO(BobaFettUser::holidaysSubscriptionOptions)
            .then(HolidaysSubscriptionOptions::dailyDistributionTime)
            .then(DistributionTime::hour);
    private static final FieldProperties<BobaFettUser, Integer> MINUTE_KEY = FR
            .chainO(BobaFettUser::holidaysSubscriptionOptions)
            .then(HolidaysSubscriptionOptions::dailyDistributionTime)
            .then(DistributionTime::minute);
    private static final FieldProperties<BobaFettUser, Boolean> IS_ACTIVE_KEY = FR
            .chainO(BobaFettUser::holidaysSubscriptionOptions)
            .then(HolidaysSubscriptionOptions::subscriptionIsActive);

    private static final FieldProperties<CustomHoliday, Long> CUSTOM_HOLIDAY_CHAT_ID_KEY = FR
            .wrap(CustomHoliday::chatId);
    private static final FieldProperties<CustomHoliday, Integer> MONTH_KEY = FR
            .wrap(CustomHoliday::month);
    private static final FieldProperties<CustomHoliday, Integer> DAY_KEY = FR
            .wrap(CustomHoliday::dayOfMonth);

    public List<BobaFettUser> findUsersToDistribution(
            MongoRepository<BobaFettUser> repository,
            int hour,
            int minute
    ) {
        MongoFilter<BobaFettUser> filter = MongoFilters.and(
                MongoFilters.exists(HOLIDAYS_SUBSCRIPTION_OPTIONS_KEY),
                MongoFilters.eq(HOUR_KEY, hour),
                MongoFilters.eq(MINUTE_KEY, minute),
                MongoFilters.eq(IS_ACTIVE_KEY, true)
        );
        return repository.find(filter);
    }

    public List<CustomHoliday> findCustomHolidaysForDistribution(
            MongoRepository<CustomHoliday> repository,
            Set<Long> forIds,
            DistributionDays distributionDays
    ) {
        MongoFilter<CustomHoliday> filter = MongoFilters.and(
                MongoFilters.or(
                        makeFilterForLocalDate(distributionDays.today()),
                        makeFilterForLocalDate(distributionDays.tomorrow()),
                        makeFilterForLocalDate(distributionDays.dayAfterTomorrow())
                ),
                MongoFilters.in(CUSTOM_HOLIDAY_CHAT_ID_KEY, forIds)
        );
        return repository.find(filter);
    }

    public List<CustomHoliday> findCustomHolidays(
            MongoRepository<CustomHoliday> repository,
            Long chatId,
            LocalDate date
    ) {
        MongoFilter<CustomHoliday> filter = MongoFilters.and(
                makeFilterForLocalDate(date),
                MongoFilters.eq(CUSTOM_HOLIDAY_CHAT_ID_KEY, chatId)
        );
        return repository.find(filter);
    }

    public List<CustomHoliday> findCustomHolidaysForReminder(
            MongoRepository<CustomHoliday> repository,
            Set<Long> forIds
    ) {
        LocalDate dateTime = LocalDate.now(DateTimeUtil.MOSCOW_ZONE_ID).plusDays(7);
        MongoFilter<CustomHoliday> filter = MongoFilters.and(
                makeFilterForLocalDate(dateTime),
                MongoFilters.in(CUSTOM_HOLIDAY_CHAT_ID_KEY, forIds)
        );
        return repository.find(filter);
    }

    private MongoFilter<CustomHoliday> makeFilterForLocalDate(LocalDate date) {
        return MongoFilters.and(
                MongoFilters.eq(MONTH_KEY, date.getMonthValue()),
                MongoFilters.eq(DAY_KEY, date.getDayOfMonth())
        );
    }
}
