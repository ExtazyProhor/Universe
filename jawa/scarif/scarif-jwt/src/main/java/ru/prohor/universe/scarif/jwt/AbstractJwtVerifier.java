package ru.prohor.universe.scarif.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;

public abstract class AbstractJwtVerifier<Payload> {
    private final ObjectMapper objectMapper;
    private final JWTVerifier verifier;
    private final Class<Payload> type;

    public AbstractJwtVerifier(
            PublicKeyProvider keyProvider,
            ObjectMapper objectMapper,
            Class<Payload> type
    ) {
        this.objectMapper = objectMapper;
        Algorithm algorithm = Algorithm.RSA256(keyProvider.getPublicKey());
        this.verifier = JWT.require(algorithm).build();
        this.type = type;
    }

    protected Opt<Payload> verifyInternal(String token) {
        try {
            DecodedJWT decodedJWT = JwtVerificationException.verify(verifier, token);
            Payload payload = objectMapper.readValue(
                    new String(Base64.decodeBase64(decodedJWT.getPayload())),
                    type
            );
            return Opt.of(payload);
        } catch (JwtVerificationException e) {
            e.printStackTrace(); // TODO
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO log.error (не должно быть вообще!) (включить в лог саму ошибку)
            System.out.println("невозможно json");
        }
        return Opt.empty();
    }
}
