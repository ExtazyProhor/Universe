package ru.prohor.universe.yahtzee.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.scarif.jwtprovider.AccessJwtProvider;
import ru.prohor.universe.yahtzee.app.web.controllers.AccountController;
import ru.prohor.universe.yahtzee.app.web.controllers.ProfileController;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@ActiveProfiles("testing")
@SpringBootTest(
        classes = YahtzeeMain.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class AccountControllerTest {
    private static final long id = new Random().nextInt();
    private static final UUID uuid = UUID.randomUUID();
    private static final ObjectId objectId = ObjectId.get();
    private static final String username = "Player " + objectId.toHexString().substring(0, 6);
    private static final String newName = "NewPlayerName";

    private final AccessJwtProvider accessJwtProvider;
    private final TestRestTemplate rest;
    private final MongoRepository<Player> playerRepository;

    public AccountControllerTest(
            @Value("${universe.test.access-token-ttl}") Duration accessTokenTtl,
            @Value("${universe.yahtzee.private-key}") String privateKey,
            @Value("${universe.yahtzee.public-key}") String publicKey,
            ObjectMapper objectMapper,
            TestRestTemplate rest,
            MongoRepository<Player> playerRepository
    ) {
        KeysFromStringProvider keysFromStringProvider = new KeysFromStringProvider(privateKey, publicKey);
        this.accessJwtProvider = new AccessJwtProvider(
                accessTokenTtl,
                keysFromStringProvider,
                objectMapper
        );
        this.rest = rest;
        this.playerRepository = playerRepository;
    }

    @BeforeEach
    public void clearDatabase() {
        playerRepository.deleteById(objectId);
    }

    @Test
    public void testAccountInfoUnauthorized() {
        ResponseEntity<AccountController.InfoResponse> response = rest.getForEntity(
                "/api/account/info",
                AccountController.InfoResponse.class
        );
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testAccountInfoSuccess() {
        HttpHeaders headers = makeHeadersWithToken();
        ResponseEntity<AccountController.InfoResponse> response = rest.exchange(
                "/api/account/info",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                AccountController.InfoResponse.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        AccountController.InfoResponse info = response.getBody();
        Assertions.assertNotNull(info);
        Assertions.assertEquals(username, info.name());
        Assertions.assertEquals(username, info.username());
        Assertions.assertFalse(info.color().randomColor());

        String colorPattern = "^[A-Fa-f0-9]{6}$";
        Assertions.assertTrue(info.color().color().matches(colorPattern));
        Assertions.assertTrue(info.color().contrast().matches(colorPattern));

        Assertions.assertDoesNotThrow(() -> new ObjectId(info.imageId()));
        Assertions.assertNull(info.room());
        Assertions.assertEquals(0, info.totalIncomingRequests());
    }

    @Test
    public void testAccountChangeNameValidName() {
        HttpHeaders headers = makeHeadersWithToken();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProfileController.ChangeNameRequest body = new ProfileController.ChangeNameRequest(newName);

        ResponseEntity<Void> response = rest.exchange(
                "/api/profile/change_name",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        headers = makeHeadersWithToken();
        ResponseEntity<AccountController.InfoResponse> infoResponse = rest.exchange(
                "/api/account/info",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                AccountController.InfoResponse.class
        );
        AccountController.InfoResponse info = infoResponse.getBody();

        Assertions.assertNotNull(info);
        Assertions.assertEquals(newName, info.name());
    }

    @Test
    public void testAccountChangeNameInvalidName() {
        HttpHeaders headers = makeHeadersWithToken();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProfileController.ChangeNameRequest body = new ProfileController.ChangeNameRequest("ab");
        ResponseEntity<Void> response = rest.exchange(
                "/api/profile/change_name",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Void.class
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private HttpHeaders makeHeadersWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + accessJwtProvider.getToken(id, uuid, objectId, ObjectId.get())
        );
        return headers;
    }
}
