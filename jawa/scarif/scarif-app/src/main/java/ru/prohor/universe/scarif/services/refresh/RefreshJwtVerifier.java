package ru.prohor.universe.scarif.services.refresh;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;
import ru.prohor.universe.scarif.jwt.StaticKeyJwtVerifier;

@Service
public class RefreshJwtVerifier extends StaticKeyJwtVerifier<RefreshJwtPayload> {
    private static final Logger log = LoggerFactory.getLogger(RefreshJwtVerifier.class);

    public RefreshJwtVerifier(PublicKeyProvider keyProvider, ObjectMapper objectMapper) {
        super("refresh", keyProvider, objectMapper, RefreshJwtPayload.class);
    }

    public Opt<RefreshToken> verify(String token) {
        return verifyInternal(token).map(payload -> {
            log.trace("received refreshToken with id {}", payload.jwtId());
            return new RefreshToken(
                    payload.userId(),
                    payload.sessionId(),
                    payload.refreshTokenId()
            );
        });
    }
}
