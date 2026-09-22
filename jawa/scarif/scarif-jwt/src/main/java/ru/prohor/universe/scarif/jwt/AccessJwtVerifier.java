package ru.prohor.universe.scarif.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;

public class AccessJwtVerifier extends StaticKeyJwtVerifier<AccessJwtPayload> {
    private static final Logger log = LoggerFactory.getLogger(AccessJwtVerifier.class);

    public AccessJwtVerifier(PublicKeyProvider keyProvider, ObjectMapper objectMapper) {
        super("access", keyProvider, objectMapper, AccessJwtPayload.class);
    }

    public Opt<AuthorizedUser> verify(String token) {
        return verifyInternal(token).map(payload -> {
            MDC.put(MDCFields.SESSION_ID_KEY, payload.sessionId());
            log.trace("received accessToken with id {}", payload.jwtId());
            return new AuthorizedUser(
                    payload.numericId(),
                    payload.uuid(),
                    payload.objectId()
            );
        });
    }
}
