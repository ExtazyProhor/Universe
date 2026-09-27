package ru.prohor.universe.scarif.services.session;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.scarif.data.RotatedRefreshToken;
import ru.prohor.universe.scarif.data.Session;
import ru.prohor.universe.scarif.data.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

final class SessionsServiceTestSupport {
    private SessionsServiceTestSupport() {}

    static User user(ObjectId id, List<Session> sessions) {
        return new User(id, UUID.randomUUID(), 1L, Instant.now(), List.of(), sessions);
    }

    static Session session(
            ObjectId id,
            Instant expiresAt,
            boolean closed,
            Opt<ObjectId> refreshToken,
            List<RotatedRefreshToken> recentlyRotatedTokens
    ) {
        return new Session(
                id, Instant.now(), expiresAt,
                Opt.empty(), "127.0.0.1", closed, Opt.empty(),
                recentlyRotatedTokens, refreshToken
        );
    }
}
