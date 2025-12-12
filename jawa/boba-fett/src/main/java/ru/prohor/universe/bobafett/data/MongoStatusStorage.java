package ru.prohor.universe.bobafett.data;

import dev.morphia.query.filters.Filters;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldReference;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionResult;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.tgbots.api.status.BotStatus;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusStorageService;

import java.util.List;

@Service
public class MongoStatusStorage implements StatusStorageService<String, String> {
    private final MongoRepository<BobaFettUser> repository;
    private final MongoTransactionService transactionService;

    public MongoStatusStorage(
            MongoRepository<BobaFettUser> repository,
            MongoTransactionService transactionService
    ) {
        this.repository = repository;
        this.transactionService = transactionService;
    }

    @Override
    public Opt<BotStatus<String, String>> getStatus(long chatId) {
        MongoTransactionResult<Opt<BotStatus<String, String>>> result = transactionService.withTransaction(tx -> {
            MongoRepository<BobaFettUser> transactional = tx.wrap(repository);
            List<BobaFettUser> users = transactional.find(
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
            BobaFettUser user = users.getFirst();
            Opt<UserStatus> status = user.status();
            if (status.isEmpty())
                return Opt.empty();
            transactional.save(user.toBuilder().status(Opt.empty()).build());
            return user.status().map(it -> new BotStatus<>(
                    it.key(),
                    it.value()
            ));
        });
        return result.asOpt().flattenO();
    }

    private String extractFieldName(FieldReference<BobaFettUser> fieldReference) {
        return fieldReference.name();
    }
}
