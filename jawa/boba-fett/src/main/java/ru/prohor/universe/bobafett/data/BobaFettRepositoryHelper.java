package ru.prohor.universe.bobafett.data;

import dev.morphia.query.filters.Filters;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldReference;
import ru.prohor.universe.jocasta.morphia.MongoRepository;

import java.util.List;

public class BobaFettRepositoryHelper {
    private BobaFettRepositoryHelper() {}

    // TODO custom filters like "eq"
    public static Opt<BobaFettUser> findByChatId(MongoRepository<BobaFettUser> repository, long chatId) {
        List<BobaFettUser> users = findUsers(repository, chatId);
        if (users.size() != 1) {
            return Opt.empty();
        }
        return Opt.of(users.getFirst());
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
