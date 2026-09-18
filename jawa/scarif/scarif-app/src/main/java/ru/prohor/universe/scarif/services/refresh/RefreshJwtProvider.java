package ru.prohor.universe.scarif.services.refresh;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;
import ru.prohor.universe.scarif.jwtprovider.AbstractJwtProvider;

import java.time.Instant;

@Service
public class RefreshJwtProvider extends AbstractJwtProvider<RefreshJwtPayload> {
    public RefreshJwtProvider(
            KeysFromStringProvider keysFromStringProvider,
            ObjectMapper objectMapper
    ) {
        super(keysFromStringProvider, objectMapper);
    }

    public String getToken(ObjectId userId, ObjectId sessionId, ObjectId refreshTokenId, Instant expires) {
        RefreshJwtPayload payload = new RefreshJwtPayload(
                userId,
                sessionId,
                refreshTokenId,
                expires,
                ObjectId.get() // TODO log "created token with id 111"
        );
        return getToken(payload);
    }
}
