package ru.prohor.universe.hyperspace.jwtprovider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.Duration;
import org.joda.time.Instant;
import ru.prohor.universe.jocasta.core.features.SnowflakeIdGenerator;
import ru.prohor.universe.hyperspace.jwt.JwtPayload;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;

import java.util.UUID;

public class JwtProvider {
    private final int accessTokenTtlMinutes;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final KeysFromStringProvider keysFromStringProvider;
    private final ObjectMapper objectMapper;

    public JwtProvider(
            int accessTokenTtlMinutes,
            SnowflakeIdGenerator snowflakeIdGenerator,
            KeysFromStringProvider keysFromStringProvider,
            ObjectMapper objectMapper
    ) {
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
        this.keysFromStringProvider = keysFromStringProvider;
        this.objectMapper = objectMapper;
    }

    public String getToken(long id, UUID uuid, String objectId, String username) {
        JwtPayload payload = new JwtPayload(
                id,
                uuid,
                objectId,
                username,
                Instant.now().plus(Duration.standardMinutes(accessTokenTtlMinutes)),
                snowflakeIdGenerator.nextId()
        );
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            Algorithm algorithm = Algorithm.RSA256(keysFromStringProvider.getPrivateKey());
            return JWT.create()
                    .withPayload(payloadJson)
                    .sign(algorithm);
        } catch (JsonProcessingException e) {
            // TODO log error
            throw new RuntimeException(e);
        }
    }
}
