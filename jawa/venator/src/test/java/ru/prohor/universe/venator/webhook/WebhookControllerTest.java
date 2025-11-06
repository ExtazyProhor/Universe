package ru.prohor.universe.venator.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestConstructor;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.venator.VenatorMain;
import ru.prohor.universe.venator.webhook.model.ApiResponse;
import ru.prohor.universe.venator.webhook.model.WebhookPayload;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

@SpringBootTest(
        classes = VenatorMain.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(WebhookControllerTest.DebugWebhookAction.class)
public class WebhookControllerTest {
    private final String secret;
    private final TestRestTemplate rest;

    public WebhookControllerTest(
            @Value("${universe.venator.webhook.secret}") String secret,
            TestRestTemplate rest
    ) {
        this.secret = secret;
        this.rest = rest;
    }

    @Test
    public void testParseWebhookPayload() throws Exception {
        String payload = getResource("/webhook-payload-mock.json");
        HttpHeaders headers = new HttpHeaders();
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret);
        headers.add("X-Hub-Signature-256", "sha256=" + hmacUtils.hmacHex(payload));
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiResponse> response = rest.exchange(
                "/webhook/on_push",
                HttpMethod.POST,
                new HttpEntity<>(payload, headers),
                ApiResponse.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        String expectedRestPayload = getResource("/webhook-rest-payload-mock.json");
        Opt<WebhookPayload> parsedPayload = DebugWebhookAction.getPayload();
        Assertions.assertTrue(parsedPayload.isPresent());

        JSONAssert.assertEquals(
                expectedRestPayload,
                new ObjectMapper().writeValueAsString(parsedPayload.get()),
                JSONCompareMode.NON_EXTENSIBLE
        );
    }

    private String getResource(String location) {
        URL url = WebhookControllerTest.class.getResource(location);
        try (InputStream stream = Objects.requireNonNull(url).openStream()) {
            return new String(stream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Service
    @Primary
    public static class DebugWebhookAction implements WebhookAction {
        private static Opt<WebhookPayload> PAYLOAD = Opt.empty();

        @Override
        public void accept(WebhookPayload payload) {
            PAYLOAD = Opt.of(payload);
        }

        public static Opt<WebhookPayload> getPayload() {
            return PAYLOAD;
        }
    }
}
