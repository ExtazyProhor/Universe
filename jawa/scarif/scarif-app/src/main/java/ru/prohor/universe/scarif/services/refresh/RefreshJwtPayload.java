package ru.prohor.universe.scarif.services.refresh;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

public record RefreshJwtPayload(
        @JsonProperty("sub")
        ObjectId userId,
        @JsonProperty("sid")
        ObjectId sessionId,
        @JsonProperty("rti")
        ObjectId refreshTokenId,
        @JsonProperty("exp")
        Instant expires,
        @JsonProperty("jti")
        ObjectId jwtId // TODO log when creating and receiving
) {}
