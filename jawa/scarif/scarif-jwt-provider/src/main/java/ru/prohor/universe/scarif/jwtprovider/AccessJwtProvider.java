package ru.prohor.universe.scarif.jwtprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;
import ru.prohor.universe.scarif.jwt.AccessJwtPayload;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class AccessJwtProvider extends AbstractJwtProvider<AccessJwtPayload> {
    private final Duration accessTokenTtl;

    public AccessJwtProvider(
            Duration accessTokenTtl,
            KeysFromStringProvider keysFromStringProvider,
            ObjectMapper objectMapper
    ) {
        super(keysFromStringProvider, objectMapper);
        this.accessTokenTtl = accessTokenTtl;
    }

    public String getToken(long numericId, UUID uuid, ObjectId objectId, ObjectId sessionId) {
        AccessJwtPayload payload = new AccessJwtPayload(
                numericId,
                uuid,
                objectId.toHexString(),
                Instant.now().plus(accessTokenTtl),
                ObjectId.get().toHexString(), // TODO log "created token with id 111"
                sessionId.toHexString()
        );
        return getToken(payload);
    }
}
