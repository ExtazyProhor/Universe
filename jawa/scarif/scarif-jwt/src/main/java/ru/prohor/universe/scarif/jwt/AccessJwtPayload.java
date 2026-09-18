package ru.prohor.universe.scarif.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record AccessJwtPayload(
        @JsonProperty("sub")
        long numericId,
        @JsonProperty("uid")
        UUID uuid,
        @JsonProperty("oid")
        String objectId,
        @JsonProperty("exp")
        Instant expires,
        @JsonProperty("sid")
        String sessionId,
        @JsonProperty("jti")
        String jwtId // TODO log when creating and receiving
) {}
