package ru.prohor.universe.scarif.oauth.google;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.scarif.oauth.exception.OAuthException;
import ru.prohor.universe.scarif.oauth.exception.OAuthServerErrorException;

@Service
public class GoogleOAuthClient {
    private final RestClient restClient;
    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public GoogleOAuthClient(RestClient restClient, GoogleOAuthProperties googleOAuthProperties) {
        this.restClient = restClient;
        this.tokenUrl = googleOAuthProperties.tokenExchangeUrl();
        this.clientId = googleOAuthProperties.clientId();
        this.clientSecret = googleOAuthProperties.clientSecret();
        this.redirectUri = googleOAuthProperties.redirectUrl();
    }

    public String getIdToken(String code) throws OAuthException {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        JsonNode response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(JsonNode.class);
        if (response == null) {
            throw new OAuthServerErrorException("Null token response from Google");
        }
        Opt<String> idToken = Opt.ofNullable(response.get("id_token"))
                .flatMapO(token -> Opt.when(token.isTextual(), token::asText));
        if (idToken.isEmpty()) {
            throw new OAuthServerErrorException("Google id token is absent or not textual");
        }
        return idToken.get();
    }
}
