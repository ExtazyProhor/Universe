package ru.prohor.universe.scarif.services;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.tools.SnowflakeIdGenerator;
import ru.prohor.universe.scarif.data.user.JpaUsersMethods;
import ru.prohor.universe.scarif.data.user.User;

import java.util.UUID;

@Service
public class UserService {
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final JpaUsersMethods usersMethods;

    public UserService(
            SnowflakeIdGenerator snowflakeIdGenerator,
            JpaUsersMethods usersMethods
            ) {
        this.snowflakeIdGenerator = snowflakeIdGenerator;
        this.usersMethods = usersMethods;
    }

    public User createUser(String username, String email, String password) {
        return new User(
                snowflakeIdGenerator.nextId(),
                UUID.randomUUID(),
                ObjectId.get(),
                username,
                email,
                password,
                true,
                Instant.now()
        );
    }

    public void register(User user) {
        usersMethods.save(user.toDto());
    }

    public Opt<User> find(long id) {
        return Opt.wrap(usersMethods.findById(id).map(User::fromDto));
    }

    public Opt<User> findByEmail(String email) {
        return Opt.wrap(usersMethods.findByEmail(email).map(User::fromDto));
    }

    public Opt<User> findByUsername(String username) {
        return Opt.wrap(usersMethods.findByUsername(username).map(User::fromDto));
    }

    public boolean existsByEmail(String email) {
        return usersMethods.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return usersMethods.existsByUsername(username);
    }
}
