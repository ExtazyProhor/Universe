package ru.prohor.universe.bobafett.data;

import dev.morphia.query.filters.Filters;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldReference;
import ru.prohor.universe.jocasta.morphia.MongoRepository;

import java.util.List;

public class BobaFettRepositoryHelper {
    private BobaFettRepositoryHelper() {}

    public static BobaFettUser findByChatId(MongoRepository<BobaFettUser> repository, long chatId) {
        List<BobaFettUser> users = repository.find(
                Filters.eq(
                        extractFieldName(BobaFettUser::chatId),
                        chatId
                ),
                user -> user.chatId() == chatId
        );
        if (users.size() != 1) {
            // TODO log err unexpected count of users with chatId = $chatId
            throw new IllegalStateException("unexpected count of users with chatId = " + chatId);
        }
        return users.getFirst();
    }

    private static String extractFieldName(FieldReference<BobaFettUser> fieldReference) {
        return fieldReference.name();
    }
}
