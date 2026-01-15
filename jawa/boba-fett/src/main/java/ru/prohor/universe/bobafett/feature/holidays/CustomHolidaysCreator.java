package ru.prohor.universe.bobafett.feature.holidays;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.bobafett.status.Statuses;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;

import java.time.LocalDate;

@Service
public class CustomHolidaysCreator {
    private static final String DATABASE_ERROR_MESSAGE = "Произошла ошибка при создании праздника, попробуйте позже";

    private final BobaFettUserService bobaFettUserService;
    private final MongoTransactionService mongoTransactionService;
    private final MongoRepository<CustomHoliday> customHolidaysRepository;
    private final MongoRepository<BobaFettUser> bobaFettUsersRepository;

    public CustomHolidaysCreator(
            BobaFettUserService bobaFettUserService,
            MongoTransactionService mongoTransactionService,
            MongoRepository<CustomHoliday> customHolidaysRepository,
            MongoRepository<BobaFettUser> bobaFettUsersRepository
    ) {
        this.bobaFettUserService = bobaFettUserService;
        this.mongoTransactionService = mongoTransactionService;
        this.customHolidaysRepository = customHolidaysRepository;
        this.bobaFettUsersRepository = bobaFettUsersRepository;
    }

    /**
     * @return message with answer for custom holidays creation
     */
    public String addNewCustomHolidayIfNotExists(long chatId, LocalDate date, String name) {
        return mongoTransactionService.withTransaction(tx -> {
            MongoRepository<CustomHoliday> txHolidaysRepository = tx.wrap(customHolidaysRepository);
            MongoRepository<BobaFettUser> txUsersRepository = tx.wrap(bobaFettUsersRepository);

            if (name.length() > 50) {
                bobaFettUserService.setStatus(
                        txUsersRepository,
                        chatId,
                        new UserStatus(Statuses.WAIT_CUSTOM_HOLIDAY_NAME, Opt.of(date.toString()))
                );
                return lengthLimit(name.length());
            }

            int day = date.getDayOfMonth();
            int month = date.getMonthValue();

            MongoFilter<CustomHoliday> filter = MongoFilters.and(
                    MongoFilters.eq(FR.wrap(CustomHoliday::chatId), chatId),
                    MongoFilters.eq(FR.wrap(CustomHoliday::holidayName), name),
                    MongoFilters.eq(FR.wrap(CustomHoliday::dayOfMonth), day),
                    MongoFilters.eq(FR.wrap(CustomHoliday::month), month)
            );

            if (!txHolidaysRepository.find(filter).isEmpty())
                return "Праздник с таким названием и датой уже существует у вас";

            CustomHoliday customHoliday = new CustomHoliday(ObjectId.get(), chatId, month, day, name);
            txHolidaysRepository.save(customHoliday);
            return "Праздник \"" + name + "\" успешно добавлен";
        }).asOpt().orElse(DATABASE_ERROR_MESSAGE);
    }

    private String lengthLimit(int length) {
        return "Название праздника превышает лимит в 50 символов - оно содержит " +
                length + " знаков. Напишите название заново или отмените " +
                "добавление с помощью команды " + Commands.CANCEL;
    }
}
