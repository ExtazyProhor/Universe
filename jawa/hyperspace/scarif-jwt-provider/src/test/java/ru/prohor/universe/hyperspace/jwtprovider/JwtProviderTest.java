package ru.prohor.universe.hyperspace.jwtprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.prohor.universe.hyperspace.jwt.AuthorizedUser;
import ru.prohor.universe.hyperspace.jwt.JwtVerifier;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.SnowflakeIdGenerator;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;
import ru.prohor.universe.jocasta.jackson.jodatime.JacksonJodaTimeConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.SnowflakeConfiguration;
import ru.prohor.universe.probe.spring.BaseSpringTest;
import ru.prohor.universe.probe.spring.TestKeysFromStringProviderConfiguration;
import ru.prohor.universe.probe.spring.TestPlaceholderProperties;

import java.util.UUID;

@SpringJUnitConfig(classes = {
        HolocronConfiguration.class,
        JacksonJodaTimeConfiguration.class,
        SnowflakeConfiguration.class,

        TestPlaceholderProperties.class,
        TestKeysFromStringProviderConfiguration.class
})
public class JwtProviderTest extends BaseSpringTest {
    private final JwtProvider jwtProvider;
    private final JwtVerifier jwtVerifier;

    public JwtProviderTest(
            ObjectMapper objectMapper,
            SnowflakeIdGenerator snowflakeIdGenerator,
            KeysFromStringProvider keysFromStringProvider,
            @Value("${universe.test.access-token-ttl-minutes}") int accessTokenTtlMinutes
    ) {
        this.jwtProvider = new JwtProvider(
                accessTokenTtlMinutes,
                snowflakeIdGenerator,
                keysFromStringProvider,
                objectMapper
        );
        this.jwtVerifier = new JwtVerifier(
                keysFromStringProvider,
                objectMapper
        );
    }

    @Test
    public void testJwtCycle() {
        long id = 123L;
        UUID uuid = UUID.fromString("f7739220-b874-4209-928b-9e1c2aee8e6c");
        String objectId = "690335fa9ba3639211b393aa";
        String username = "TestUser";

        String token = jwtProvider.getToken(id, uuid, objectId, username);
        Assertions.assertNotNull(token);

        Opt<AuthorizedUser> authorizedUserO = jwtVerifier.verify(token);
        Assertions.assertNotNull(authorizedUserO);
        Assertions.assertTrue(authorizedUserO.isPresent());
        AuthorizedUser authorizedUser = authorizedUserO.get();

        Assertions.assertEquals(id, authorizedUser.id());
        Assertions.assertEquals(uuid, authorizedUser.uuid());
        Assertions.assertEquals(objectId, authorizedUser.objectId());
        Assertions.assertEquals(username, authorizedUser.username());
    }
}
