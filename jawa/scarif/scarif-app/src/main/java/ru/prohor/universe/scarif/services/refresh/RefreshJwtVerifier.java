package ru.prohor.universe.scarif.services.refresh;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;
import ru.prohor.universe.scarif.jwt.AbstractJwtVerifier;

@Service
public class RefreshJwtVerifier extends AbstractJwtVerifier<RefreshJwtPayload> {
    public RefreshJwtVerifier(PublicKeyProvider keyProvider, ObjectMapper objectMapper) {
        super(keyProvider, objectMapper, RefreshJwtPayload.class);
    }

    public Opt<RefreshToken> verify(String token) {
        return verifyInternal(token).map(payload -> new RefreshToken(
                payload.userId(),
                payload.sessionId(),
                payload.refreshTokenId()
        ));
    }
}
