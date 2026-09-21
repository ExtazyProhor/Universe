package ru.prohor.universe.scarif.services.refresh;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;
import ru.prohor.universe.scarif.jwtprovider.AbstractJwtProvider;

import java.time.Instant;

@Service
public class RefreshJwtProvider extends AbstractJwtProvider<RefreshJwtPayload> {
    private static final Logger log = LoggerFactory.getLogger(RefreshJwtProvider.class);

    public RefreshJwtProvider(
            KeysFromStringProvider keysFromStringProvider,
            ObjectMapper objectMapper
    ) {
        super(keysFromStringProvider, objectMapper);
    }

    public String getToken(ObjectId userId, ObjectId sessionId, ObjectId refreshTokenId, Instant expires) {
        ObjectId jwtId = ObjectId.get();
        log.trace("generated refresh jwt with id: {}", jwtId);
        RefreshJwtPayload payload = new RefreshJwtPayload(
                userId,
                sessionId,
                refreshTokenId,
                expires,
                jwtId
        );
        return getToken(payload);
    }
}
