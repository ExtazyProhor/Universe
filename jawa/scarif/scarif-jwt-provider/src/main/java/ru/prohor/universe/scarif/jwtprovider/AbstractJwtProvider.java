package ru.prohor.universe.scarif.jwtprovider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;

public abstract class AbstractJwtProvider<Payload> {
    private final ObjectMapper objectMapper;
    private final Algorithm algorithm;

    public AbstractJwtProvider(
            KeysFromStringProvider keysFromStringProvider,
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.algorithm = Algorithm.RSA256(keysFromStringProvider.getPrivateKey());
    }

    protected String getToken(Payload payload) {
        String payloadJson = Sneaky.execute(() -> objectMapper.writeValueAsString(payload));
        return JWT.create().withPayload(payloadJson).sign(algorithm);
    }
}
