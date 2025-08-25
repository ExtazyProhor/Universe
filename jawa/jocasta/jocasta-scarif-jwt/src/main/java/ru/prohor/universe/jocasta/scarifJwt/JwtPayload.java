package ru.prohor.universe.jocasta.scarifJwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.Instant;

import java.util.UUID;

public record JwtPayload(
        @JsonProperty("sub")
        long id,
        @JsonProperty("uid")
        UUID uuid,
        @JsonProperty("oid")
        String objectId,
        @JsonProperty("usr")
        String username,
        @JsonProperty("exp")
        Instant expires,
        @JsonProperty("jti")
        long jwtId
) {}
