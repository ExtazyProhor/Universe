package ru.prohor.universe.bobafett.data;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransactionResult;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusStorage;

@Service
public class MongoStatusStorage implements ValuedStatusStorage<String, String> {
    private final MongoRepository<BobaFettUser> repository;
    private final MongoTransactionService transactionService;

    public MongoStatusStorage(
            MongoRepository<BobaFettUser> repository,
            MongoTransactionService transactionService
    ) {
        this.repository = repository;
        this.transactionService = transactionService;
    }

    public boolean setStatus(long chatId, ValuedStatus<String, String> status) {
        MongoTransactionResult<Boolean> result = transactionService.withTransaction(tx -> {
            MongoRepository<BobaFettUser> transactional = tx.wrap(repository);

            Opt<BobaFettUser> userO = BobaFettRepositoryHelper.findByChatId(transactional, chatId);
            if (userO.isEmpty()) {
                // TODO log err unexpected count of users with chatId = $chatId
                return false;
            }
            BobaFettUser user = userO.get();
            UserStatus userStatus = new UserStatus(status.key(), status.value());
            transactional.save(user.toBuilder().status(Opt.of(userStatus)).build());
            return true;
        });
        return result.asOpt().orElse(false);
    }

    @Override
    public Opt<ValuedStatus<String, String>> getStatus(long chatId) {
        MongoTransactionResult<Opt<ValuedStatus<String, String>>> result = transactionService.withTransaction(tx -> {
            MongoRepository<BobaFettUser> transactional = tx.wrap(repository);

            Opt<BobaFettUser> userO = BobaFettRepositoryHelper.findByChatId(transactional, chatId);
            if (userO.isEmpty()) {
                // TODO log err unexpected count of users with chatId = $chatId
                return Opt.empty();
            }
            BobaFettUser user = userO.get();
            Opt<UserStatus> status = user.status();
            if (status.isEmpty())
                return Opt.empty();
            transactional.save(user.toBuilder().status(Opt.empty()).build());
            return user.status().map(it -> new ValuedStatus<>(
                    it.key(),
                    it.value()
            ));
        });
        return result.asOpt().flattenO();
    }
}
