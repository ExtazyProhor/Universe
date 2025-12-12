package ru.prohor.universe.bobafett.data;

import dev.morphia.query.filters.Filters;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldReference;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.status.BotStatus;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusStorageService;

import java.util.List;

@Service
public class MongoStatusStorage implements StatusStorageService<String, String> {
    private final MongoRepository<BobaFettUser> repository;

    public MongoStatusStorage(MongoRepository<BobaFettUser> repository) {
        this.repository = repository;
    }

    @Override
    public Opt<BotStatus<String, String>> getStatus(long chatId) {
        List<BobaFettUser> users = repository.find(
                Filters.eq(
                        extractFieldName(BobaFettUser::chatId),
                        chatId
                ),
                user -> user.chatId() == chatId
        );
        if (users.size() != 1) {
            // TODO log err unexpected count of users with chatId = $chatId
            return Opt.empty();
        }
        // TODO transaction
        return users.getFirst().status().map(it -> new BotStatus<>(
                it.key(),
                it.value()
        ));
    }

    private String extractFieldName(FieldReference<BobaFettUser> fieldReference) {
        return fieldReference.name();
    }
}
