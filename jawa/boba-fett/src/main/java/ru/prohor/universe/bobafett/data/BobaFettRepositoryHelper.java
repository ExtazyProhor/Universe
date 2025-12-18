package ru.prohor.universe.bobafett.data;

import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import org.joda.time.LocalDate;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.data.pojo.DistributionTime;
import ru.prohor.universe.bobafett.data.pojo.HolidaysSubscriptionOptions;
import ru.prohor.universe.bobafett.feature.holidays.DistributionDays;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldReference;
import ru.prohor.universe.jocasta.core.features.fieldref.InnerFieldReference;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;

import java.util.List;
import java.util.Set;

public class BobaFettRepositoryHelper {
    private static final String HOUR_KEY = InnerFieldReference.createO(BobaFettUser::holidaysSubscriptionOptions)
            .then(HolidaysSubscriptionOptions::dailyDistributionTime)
            .then(DistributionTime::hour)
            .name();
    private static final String MINUTE_KEY = InnerFieldReference.createO(BobaFettUser::holidaysSubscriptionOptions)
            .then(HolidaysSubscriptionOptions::dailyDistributionTime)
            .then(DistributionTime::minute)
            .name();
    private static final String IS_ACTIVE_KEY = InnerFieldReference.createO(BobaFettUser::holidaysSubscriptionOptions)
            .then(HolidaysSubscriptionOptions::subscriptionIsActive)
            .name();

    private static final String CHAT_ID_KEY = FieldReference.name(CustomHoliday::chatId);
    private static final String MONTH_KEY = FieldReference.name(CustomHoliday::month);
    private static final String DAY_KEY = FieldReference.name(CustomHoliday::dayOfMonth);

    private BobaFettRepositoryHelper() {}

    // TODO custom filters like "eq"
    public static Opt<BobaFettUser> findByChatId(MongoRepository<BobaFettUser> repository, long chatId) {
        List<BobaFettUser> users = findUsers(repository, chatId);
        if (users.size() != 1) {
            return Opt.empty();
        }
        return Opt.of(users.getFirst());
    }

    public static List<BobaFettUser> findUsersToDistribution(
            MongoRepository<BobaFettUser> repository,
            int hour,
            int minute
    ) {
        Filter filter = Filters.and(
                Filters.exists(FieldReference.name(BobaFettUser::holidaysSubscriptionOptions)),
                Filters.eq(HOUR_KEY, hour),
                Filters.eq(MINUTE_KEY, minute),
                Filters.eq(IS_ACTIVE_KEY, true)
        );
        MonoPredicate<BobaFettUser> manualFilter = user -> {
            if (user.holidaysSubscriptionOptions().isEmpty())
                return false;
            HolidaysSubscriptionOptions options = user.holidaysSubscriptionOptions().get();
            DistributionTime time = options.dailyDistributionTime();
            return options.subscriptionIsActive() && time.hour() == hour && time.minute() == minute;
        };
        return repository.find(filter, manualFilter);
    }

    public static List<CustomHoliday> findCustomHolidaysForDistribution(
            MongoRepository<CustomHoliday> repository,
            Set<Long> forIds,
            DistributionDays distributionDays
    ) {
        Filter filter = Filters.and(
                Filters.or(
                        makeFilterForLocalDate(distributionDays.today()),
                        makeFilterForLocalDate(distributionDays.tomorrow()),
                        makeFilterForLocalDate(distributionDays.dayAfterTomorrow())
                ),
                Filters.in(CHAT_ID_KEY, forIds)
        );
        MonoPredicate<CustomHoliday> manualFilter = holiday -> {
            if (!forIds.contains(holiday.chatId()))
                return false;
            if (isHolidaySuitableForDate(holiday, distributionDays.today()))
                return true;
            if (isHolidaySuitableForDate(holiday, distributionDays.tomorrow()))
                return true;
            return isHolidaySuitableForDate(holiday, distributionDays.dayAfterTomorrow());
        };
        return repository.find(filter, manualFilter);
    }

    public static List<CustomHoliday> findCustomHolidaysForReminder(
            MongoRepository<CustomHoliday> repository,
            Set<Long> forIds
    ) {
        LocalDate dateTime = LocalDate.now(DateTimeUtil.zoneMoscow()).plusDays(7);
        Filter filter = Filters.and(
                makeFilterForLocalDate(dateTime),
                Filters.in(CHAT_ID_KEY, forIds)
        );
        MonoPredicate<CustomHoliday> manualFilter = holiday -> forIds.contains(holiday.chatId())
                && isHolidaySuitableForDate(holiday, dateTime);
        return repository.find(filter, manualFilter);
    }

    private static boolean isHolidaySuitableForDate(CustomHoliday holiday, LocalDate date) {
        return holiday.month() == date.getMonthOfYear() && holiday.dayOfMonth() == date.getDayOfMonth();
    }

    private static Filter makeFilterForLocalDate(LocalDate date) {
        return Filters.and(
                Filters.eq(MONTH_KEY, date.getMonthOfYear()),
                Filters.eq(DAY_KEY, date.getDayOfMonth())
        );
    }

    public static List<CustomHoliday> findCustomHolidaysByChatId(
            MongoRepository<CustomHoliday> repository,
            long chatId
    ) {
        return repository.find(
                Filters.eq(
                        ((FieldReference<CustomHoliday>) CustomHoliday::chatId).name(),
                        chatId
                ),
                holiday -> holiday.chatId() == chatId
        );
    }

    public static boolean containsByChatId(MongoRepository<BobaFettUser> repository, long chatId) {
        return !findUsers(repository, chatId).isEmpty();
    }

    private static List<BobaFettUser> findUsers(MongoRepository<BobaFettUser> repository, long chatId) {
        return repository.find(
                Filters.eq(
                        ((FieldReference<BobaFettUser>) BobaFettUser::chatId).name(),
                        chatId
                ),
                user -> user.chatId() == chatId
        );
    }
}
