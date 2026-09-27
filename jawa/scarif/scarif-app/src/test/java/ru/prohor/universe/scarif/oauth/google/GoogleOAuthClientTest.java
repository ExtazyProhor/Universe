package ru.prohor.universe.scarif.oauth.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.prohor.universe.scarif.oauth.exception.OAuthException;
import ru.prohor.universe.scarif.oauth.exception.OAuthServerErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.CLIENT_ID;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.mockRestClientForPost;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.mockRestClientForPostThrowing;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.properties;

class GoogleOAuthClientTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static ObjectNode responseWithIdToken(String idToken) {
        ObjectNode node = MAPPER.createObjectNode();
        if (idToken != null) {
            node.put("id_token", idToken);
        }
        return node;
    }

    @Test
    void returnsIdTokenFromResponse() throws Exception {
        RestClient restClient = mockRestClientForPost(responseWithIdToken("the-id-token"));
        GoogleOAuthClient client = new GoogleOAuthClient(restClient, properties());

        assertEquals("the-id-token", client.getIdToken("auth-code"));
    }

    @Test
    void nullResponseThrows() {
        RestClient restClient = mockRestClientForPost(null);
        GoogleOAuthClient client = new GoogleOAuthClient(restClient, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> client.getIdToken("auth-code"));
        assertInstanceOf(OAuthServerErrorException.class, e);
        assertTrue(e.getMessage().contains("Null token response"));
    }

    @Test
    void missingIdTokenFieldThrows() {
        RestClient restClient = mockRestClientForPost(responseWithIdToken(null));
        GoogleOAuthClient client = new GoogleOAuthClient(restClient, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> client.getIdToken("auth-code"));
        assertTrue(e.getMessage().contains("id token is absent"));
    }

    @Test
    void nonTextualIdTokenFieldThrows() {
        ObjectNode body = MAPPER.createObjectNode();
        body.put("id_token", 12345);
        RestClient restClient = mockRestClientForPost(body);
        GoogleOAuthClient client = new GoogleOAuthClient(restClient, properties());

        assertThrows(OAuthException.class, () -> client.getIdToken("auth-code"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendsExpectedFormParameters() throws Exception {
        RestClient restClient = mockRestClientForPost(responseWithIdToken("tok"));
        GoogleOAuthClient client = new GoogleOAuthClient(restClient, properties());

        client.getIdToken("auth-code-xyz");

        RestClient.RequestBodyUriSpec bodyUriSpec = restClient.post();
        ArgumentCaptor<MultiValueMap<String, String>> captor = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(bodyUriSpec).body(captor.capture());
        MultiValueMap<String, String> form = captor.getValue();

        assertEquals("auth-code-xyz", form.getFirst("code"));
        assertEquals(CLIENT_ID, form.getFirst("client_id"));
        assertEquals("test-client-secret", form.getFirst("client_secret"));
        assertEquals("authorization_code", form.getFirst("grant_type"));
        assertNotNull(form.getFirst("redirect_uri"));
    }

    @Test
    void restClientFailurePropagatesUnwrapped() {
        RestClient restClient = mockRestClientForPostThrowing(new RestClientException("network boom"));
        GoogleOAuthClient client = new GoogleOAuthClient(restClient, properties());

        assertThrows(RestClientException.class, () -> client.getIdToken("auth-code"));
    }
}
