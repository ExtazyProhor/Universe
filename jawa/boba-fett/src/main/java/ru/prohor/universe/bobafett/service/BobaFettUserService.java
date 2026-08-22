package ru.prohor.universe.bobafett.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.UserStatus;
import ru.prohor.universe.bobafett.feature.currency.CurrencyService;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;

import java.util.List;

@Service
public class BobaFettUserService {
    private final MongoRepository<BobaFettUser> usersRepository;
    private final CurrencyService currencyService;

    public BobaFettUserService(
            MongoRepository<BobaFettUser> usersRepository,
            CurrencyService currencyService
    ) {
        this.usersRepository = usersRepository;
        this.currencyService = currencyService;
    }

    public BobaFettUser create(Chat chat) {
        String name = chat.isUserChat() ? chat.getFirstName() : chat.getTitle();
        Opt<String> link = Opt.when(
                chat.isUserChat() && chat.getUserName() != null,
                () -> "@" + chat.getUserName()
        );
        return new BobaFettUser(
                ObjectId.get(),
                chat.getId(),
                chat.getType(),
                Opt.ofNullable(name),
                link,
                Opt.empty(),
                currencyService.createOptions(),
                Opt.empty(),
                true
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

    public void createIfNotExists(Chat chat) {
        usersRepository.withTransaction(tx -> {
            Opt<BobaFettUser> user = findByChatId(tx, chat.getId());
            if (user.isEmpty()) {
                tx.save(create(chat));
                return;
            }
            if (!user.get().enabled()) {
                tx.save(user.get().toBuilder().enabled(true).build());
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

    public BobaFettUser ensureFindByChatId(long chatId) {
        return ensureFindByChatId(usersRepository, chatId);
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
