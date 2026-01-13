package ru.prohor.universe.bobafett.feature.holidays;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;

import java.util.List;

@Service
public class CustomHolidaysService {
    private final MongoRepository<CustomHoliday> customHolidaysRepository;

    public CustomHolidaysService(MongoRepository<CustomHoliday> customHolidaysRepository) {
        this.customHolidaysRepository = customHolidaysRepository;
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
}
