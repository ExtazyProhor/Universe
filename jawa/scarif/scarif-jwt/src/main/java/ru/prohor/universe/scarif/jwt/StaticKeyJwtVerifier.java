package ru.prohor.universe.scarif.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;

public abstract class StaticKeyJwtVerifier<Payload> extends AbstractJwtVerifier {
    private static final Logger log = LoggerFactory.getLogger(StaticKeyJwtVerifier.class);

    private final String tokenNameForLogs;
    private final ObjectMapper objectMapper;
    private final JWTVerifier verifier;
    private final Class<Payload> type;

    public StaticKeyJwtVerifier(
            String tokenNameForLogs,
            PublicKeyProvider keyProvider,
            ObjectMapper objectMapper,
            Class<Payload> type
    ) {
        this.tokenNameForLogs = tokenNameForLogs;
        this.objectMapper = objectMapper;
        Algorithm algorithm = Algorithm.RSA256(keyProvider.getPublicKey());
        this.verifier = JWT.require(algorithm).build();
        this.type = type;
    }

    protected Opt<Payload> verifyInternal(String token) {
        try {
            DecodedJWT decodedJWT = verify(verifier, token);
            Payload payload = Sneaky.execute(() -> objectMapper.readValue(
                    new String(Base64.decodeBase64(decodedJWT.getPayload())),
                    type
            ));
            return Opt.of(payload);
        } catch (JwtVerificationException e) {
            if (e.isWarning()) {
                log.warn("Error when verifying {} jwt", tokenNameForLogs, e);
            } else {
                log.trace("{} jwt: {}", tokenNameForLogs, e.getMessage());
            }
        }
        return Opt.empty();
    }
}
