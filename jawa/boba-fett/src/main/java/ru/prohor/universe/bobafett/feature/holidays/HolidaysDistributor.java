package ru.prohor.universe.bobafett.feature.holidays;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.BobaFettRepositoryHelper;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.data.pojo.DistributionTime;
import ru.prohor.universe.bobafett.data.pojo.HolidaysSubscriptionOptions;
import ru.prohor.universe.bobafett.distribution.DistributionTask;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldProperties;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HolidaysDistributor implements DistributionTask {
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

    private final MongoTransactionService mongoTransactionService;
    private final MongoRepository<BobaFettUser> bobaFettUsersRepository;
    private final MongoRepository<CustomHoliday> customHolidaysRepository;
    private final HolidaysMessageFormatter holidaysMessageFormatter;
    private final HolidaysService holidaysService;

    public HolidaysDistributor(
            MongoTransactionService mongoTransactionService,
            MongoRepository<BobaFettUser> bobaFettUsersRepository,
            MongoRepository<CustomHoliday> customHolidaysRepository,
            HolidaysMessageFormatter holidaysMessageFormatter,
            HolidaysService holidaysService
    ) {
        this.mongoTransactionService = mongoTransactionService;
        this.bobaFettUsersRepository = bobaFettUsersRepository;
        this.customHolidaysRepository = customHolidaysRepository;
        this.holidaysMessageFormatter = holidaysMessageFormatter;
        this.holidaysService = holidaysService;
    }

    @Override
    public void distribute(FeedbackExecutor feedbackExecutor, int hour, int minute) {
        mongoTransactionService.withTransaction(tx -> {
            MongoRepository<BobaFettUser> txUsersRepository = tx.wrap(bobaFettUsersRepository);
            MongoRepository<CustomHoliday> txHolidaysRepository = tx.wrap(customHolidaysRepository);

            List<BobaFettUser> users = findUsersToDistribution(
                    txUsersRepository,
                    hour,
                    minute
            );
            if (users.isEmpty())
                return;

            Set<Long> forIds = users.stream().map(BobaFettUser::chatId).collect(Collectors.toSet());
            DistributionDays distributionDays = DistributionDays.create();
            List<CustomHoliday> holidays = BobaFettRepositoryHelper.findCustomHolidaysForDistribution(
                    txHolidaysRepository,
                    forIds,
                    distributionDays
            ); // TODO делать одним запросом
            List<CustomHoliday> holidaysForReminder = BobaFettRepositoryHelper.findCustomHolidaysForReminder(
                    txHolidaysRepository,
                    forIds
            );

            StructuredCustomHolidays structuredCustomHolidays = new StructuredCustomHolidays(
                    distributionDays,
                    holidays
            );
            Map<Long, List<String>> holidaysForReminderByChatId = holidaysForReminder.stream().collect(
                    Collectors.groupingBy(
                            CustomHoliday::chatId,
                            Collectors.mapping(
                                    CustomHoliday::holidayName,
                                    Collectors.toList()
                            )
                    )
            );

            for (BobaFettUser user : users) {
                long chatId = user.chatId();
                int indent = user.holidaysSubscriptionOptions().get().indentationOfDays();
                LocalDate date = distributionDays.getLocalDate(indent);

                feedbackExecutor.sendMessage(
                        chatId,
                        holidaysService.getHolidaysMessageForDate(
                                date,
                                distributionDays.today().getYear(),
                                structuredCustomHolidays.getHolidays(chatId, indent)
                        )
                );

                Opt.ofNullable(holidaysForReminderByChatId.get(chatId)).ifPresent(
                        list -> feedbackExecutor.sendMessage(
                                chatId,
                                holidaysMessageFormatter.formatReminder(list)
                        )
                );
            }
        });
    }

    private List<BobaFettUser> findUsersToDistribution(
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

    private static class StructuredCustomHolidays {
        private final Map<Long, Map<Integer, List<String>>> holidaysByChatId;

        public StructuredCustomHolidays(DistributionDays distributionDays, List<CustomHoliday> customHolidays) {
            this.holidaysByChatId = customHolidays.stream().collect(
                    Collectors.groupingBy(
                            CustomHoliday::chatId,
                            Collectors.groupingBy(
                                    distributionDays::getIndent,
                                    Collectors.mapping(
                                            CustomHoliday::holidayName,
                                            Collectors.toList()
                                    )
                            )
                    )
            );
        }

        public Opt<List<String>> getHolidays(Long chatId, Integer indent) {
            return Opt.ofNullable(
                    holidaysByChatId.get(chatId)
            ).flatMapO(
                    map -> Opt.ofNullable(map.get(indent))
            );
        }
    }
}
