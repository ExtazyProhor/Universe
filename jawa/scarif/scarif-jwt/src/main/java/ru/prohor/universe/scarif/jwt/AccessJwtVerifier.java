package ru.prohor.universe.scarif.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;

public class AccessJwtVerifier extends AbstractJwtVerifier<AccessJwtPayload> {
    public AccessJwtVerifier(PublicKeyProvider keyProvider, ObjectMapper objectMapper) {
        super(keyProvider, objectMapper, AccessJwtPayload.class);
    }

    public Opt<AuthorizedUser> verify(String token) {
        // TODO log "получен access-token с таким-то id с таким-то sessionId"
        return verifyInternal(token).map(payload -> new AuthorizedUser(
                payload.numericId(),
                payload.uuid(),
                payload.objectId()
        ));
    }
}
