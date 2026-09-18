package ru.prohor.universe.scarif.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenResponse(
        @JsonProperty("access_token")
        String accessToken
) {}
