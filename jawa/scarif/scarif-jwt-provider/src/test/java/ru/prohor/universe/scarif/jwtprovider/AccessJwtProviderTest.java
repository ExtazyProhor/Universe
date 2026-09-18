package ru.prohor.universe.scarif.jwtprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.jackson.JacksonJocastaCoreConfiguration;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;
import ru.prohor.universe.jocasta.spring.configuration.HolocronConfiguration;
import ru.prohor.universe.jocasta.spring.configuration.SnowflakeConfiguration;
import ru.prohor.universe.scarif.jwt.AccessJwtVerifier;
import ru.prohor.universe.scarif.jwt.AuthorizedUser;

import java.time.Duration;
import java.util.UUID;

@SpringBootTest
@SpringJUnitConfig(classes = {
        HolocronConfiguration.class,
        JacksonJocastaCoreConfiguration.class,
        SnowflakeConfiguration.class,

        AccessJwtProviderTest.Configuration.class
})
@TestPropertySource("classpath:application.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class AccessJwtProviderTest {
    private final AccessJwtProvider accessJwtProvider;
    private final AccessJwtVerifier accessJwtVerifier;

    public AccessJwtProviderTest(
            ObjectMapper objectMapper,
            KeysFromStringProvider keysFromStringProvider,
            @Value("${universe.test.access-token-ttl}") Duration accessTokenTtl
    ) {
        this.accessJwtProvider = new AccessJwtProvider(
                accessTokenTtl,
                keysFromStringProvider,
                objectMapper
        );
        this.accessJwtVerifier = new AccessJwtVerifier(
                keysFromStringProvider,
                objectMapper
        );
    }

    @Test
    public void testJwtCycle() {
        long id = 123L;
        UUID uuid = UUID.fromString("f7739220-b874-4209-928b-9e1c2aee8e6c");
        ObjectId objectId = new ObjectId("690335fa9ba3639211b393aa");
        ObjectId sessionId = new ObjectId("690335fa9ba3639211b393ab");

        String token = accessJwtProvider.getToken(id, uuid, objectId, sessionId);
        Assertions.assertNotNull(token);

        Opt<AuthorizedUser> authorizedUserO = accessJwtVerifier.verify(token);
        Assertions.assertNotNull(authorizedUserO);
        Assertions.assertTrue(authorizedUserO.isPresent());
        AuthorizedUser authorizedUser = authorizedUserO.get();

        Assertions.assertEquals(id, authorizedUser.numericId());
        Assertions.assertEquals(uuid, authorizedUser.uuid());
        Assertions.assertEquals(objectId, new ObjectId(authorizedUser.objectId()));
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public KeysFromStringProvider keysFromStringProvider(
                @Value("${universe.test.private-key}") String privateKey,
                @Value("${universe.test.public-key}") String publicKey
        ) {
            return new KeysFromStringProvider(privateKey, publicKey);
        }

        @Bean
        static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
}
