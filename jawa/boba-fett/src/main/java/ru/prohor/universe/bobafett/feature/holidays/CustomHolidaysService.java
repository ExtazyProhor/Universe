package ru.prohor.universe.bobafett.feature.holidays;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;

import java.util.List;

@Service
public class CustomHolidaysService {
    private static final String DATABASE_ERROR_MESSAGE = "Произошла ошибка при создании праздника, попробуйте позже";

    private final MongoTransactionService mongoTransactionService;
    private final MongoRepository<CustomHoliday> customHolidaysRepository;

    public CustomHolidaysService(
            MongoTransactionService mongoTransactionService,
            MongoRepository<CustomHoliday> customHolidaysRepository
    ) {
        this.mongoTransactionService = mongoTransactionService;
        this.customHolidaysRepository = customHolidaysRepository;
    }

    /**
     * @return message with answer for custom holidays creation
     */
    public String addNewCustomHolidayIfNotExists(long chatId, int day, int month, String name) {
        if (name.length() > 50) {
            return lengthLimit(name.length());
        }

        return mongoTransactionService.withTransaction(tx -> {
            MongoRepository<CustomHoliday> wrapped = tx.wrap(customHolidaysRepository);
            MongoFilter<CustomHoliday> filter = MongoFilters.and(
                    MongoFilters.eq(FR.wrap(CustomHoliday::chatId), chatId),
                    MongoFilters.eq(FR.wrap(CustomHoliday::holidayName), name),
                    MongoFilters.eq(FR.wrap(CustomHoliday::dayOfMonth), day),
                    MongoFilters.eq(FR.wrap(CustomHoliday::month), month)
            );

            if (!wrapped.find(filter).isEmpty())
                return "Праздник с таким названием и датой уже существует у вас";

            CustomHoliday customHoliday = new CustomHoliday(ObjectId.get(), chatId, month, day, name);
            wrapped.save(customHoliday);
            return "Праздник \"" + name + "\" успешно добавлен";
        }).asOpt().orElse(DATABASE_ERROR_MESSAGE);
    }

    public Opt<CustomHoliday> deleteCustomHoliday(String id) {
        return customHolidaysRepository.deleteById(new ObjectId(id));
    }

    public List<CustomHoliday> findCustomHolidays(long chatId) {
        return customHolidaysRepository.find(filterByChatId(chatId));
    }

    public List<CustomHoliday> findCustomHolidays(MongoRepository<CustomHoliday> repository, long chatId) {
        return repository.find(filterByChatId(chatId));
    }

    private MongoFilter<CustomHoliday> filterByChatId(long chatId) {
        return MongoFilters.eq(FR.wrap(CustomHoliday::chatId), chatId);
    }

    private String lengthLimit(int length) {
        // TODO заново не читается, надо добавлять статус ручками
        return "Название праздника превышает лимит в 50 символов - оно содержит " +
                length + " знаков. Напишите название заново или отмените " +
                "добавление с помощью команды " + Commands.CANCEL;
    }
}
