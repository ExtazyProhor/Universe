package ru.prohor.universe.scarif.services;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.SnowflakeIdGenerator;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;
import ru.prohor.universe.scarif.data.ExternalAccount;
import ru.prohor.universe.scarif.data.ExternalAccountProvider;
import ru.prohor.universe.scarif.data.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public UserService(SnowflakeIdGenerator snowflakeIdGenerator) {
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    public Opt<User> findByExternalAccount(
            MongoRepository<User> usersRepository,
            ExternalAccountProvider provider,
            String id
    ) {
        MongoFilter<User> filter = MongoFilters.elemMatch(
                FR.wrap(User::externalAccounts),
                MongoFilters.and(
                        MongoFilters.eq(FR.wrap(ExternalAccount::provider), provider),
                        MongoFilters.eq(FR.wrap(ExternalAccount::id), id)
                )
        );
        return usersRepository.findOne(filter);
    }

    public User createUser() {
        return new User(
                ObjectId.get(),
                UUID.randomUUID(),
                snowflakeIdGenerator.nextId(),
                Instant.now(),
                List.of(),
                List.of()
        );
    }
}
