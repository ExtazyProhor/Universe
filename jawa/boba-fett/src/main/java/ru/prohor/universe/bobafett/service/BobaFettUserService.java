package ru.prohor.universe.bobafett.service;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.HolidaysSubscriptionOptions;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.NilFunction;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;

import java.util.List;

@Service
public class BobaFettUserService {
    private final MongoRepository<BobaFettUser> usersRepository;

    public BobaFettUserService(MongoRepository<BobaFettUser> usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void disableHolidaysSubscription(long chatId) {
        usersRepository.safeUpdate(
                filterByChatId(chatId),
                user -> {
                    Opt<HolidaysSubscriptionOptions> disabled = user.holidaysSubscriptionOptions().map(
                            it -> it.toBuilder().subscriptionIsActive(false).build()
                    );
                    return user.toBuilder()
                            .holidaysSubscriptionOptions(disabled)
                            .build();
                }
        );
    }

    public void changeChatId(long oldChatId, long newChatId) {
        usersRepository.safeUpdate(
                filterByChatId(oldChatId),
                user -> user.toBuilder()
                        .chatId(newChatId)
                        .build()
        );
    }

    public void createIfNotExists(long chatId, NilFunction<BobaFettUser> userProvider) {
        usersRepository.withTransaction(tx -> {
            if (!contains(tx, chatId)) {
                tx.save(userProvider.apply());
            }
        });
    }

    public Opt<UserStatus> getStatusAndRemoveIt(long chatId) {
        return usersRepository.withTransaction(tx -> {
            Opt<BobaFettUser> user = findByChatId(tx, chatId);
            if (user.isEmpty())
                return Opt.empty();

            Opt<UserStatus> status = user.get().status();
            if (status.isEmpty())
                return Opt.empty();
            tx.save(user.get().toBuilder().status(Opt.empty()).build());
            return status;
        });
    }

    public void setStatus(long chatId, UserStatus status) {
        setStatus(usersRepository, chatId, status);
    }

    public void setStatus(MongoRepository<BobaFettUser> repository, long chatId, UserStatus status) {
        repository.withTransaction(tx -> {
            Opt<BobaFettUser> user = findByChatId(tx, chatId);
            user.ifPresent(
                    it -> tx.save(it.toBuilder().status(Opt.of(status)).build())
            );
        });
    }

    public void safeUpdate(long chatId, MonoFunction<BobaFettUser, BobaFettUser> updateFunction) {
        usersRepository.safeUpdate(filterByChatId(chatId), updateFunction);
    }

    public BobaFettUser ensureFindByChatId(MongoRepository<BobaFettUser> repository, long chatId) {
        return findByChatId(repository, chatId).orElseThrow(
                () -> new RuntimeException("Unexpected count of users with chatId=" + chatId)
        );
    }

    public Opt<BobaFettUser> findByChatId(MongoRepository<BobaFettUser> repository, long chatId) {
        return Opt.of(repository.find(filterByChatId(chatId)))
                .filter(list -> list.size() == 1)
                .map(List::getFirst);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(MongoRepository<BobaFettUser> repository, long chatId) {
        return !repository.find(filterByChatId(chatId)).isEmpty();
    }

    private MongoFilter<BobaFettUser> filterByChatId(long chatId) {
        return MongoFilters.eq(FR.wrap(BobaFettUser::chatId), chatId);
    }
}
