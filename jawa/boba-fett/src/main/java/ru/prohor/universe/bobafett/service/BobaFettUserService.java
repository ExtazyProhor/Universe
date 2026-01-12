package ru.prohor.universe.bobafett.service;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;

@Service
public class BobaFettUserService {
    private final MongoRepository<BobaFettUser> usersRepository;

    public BobaFettUserService(MongoRepository<BobaFettUser> usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void disableHolidaysSubscription(long chatId) {
        usersRepository.safeUpdate(
                filterByChatId(chatId),
                user -> user.toBuilder()
                        .holidaysSubscriptionOptions(
                                user.holidaysSubscriptionOptions().map(
                                        it -> it.toBuilder().subscriptionIsActive(false).build()
                                )
                        )
                        .build()
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

    public MongoFilter<BobaFettUser> filterByChatId(long chatId) {
        return MongoFilters.eq(FR.wrap(BobaFettUser::chatId), chatId);
    }
}
