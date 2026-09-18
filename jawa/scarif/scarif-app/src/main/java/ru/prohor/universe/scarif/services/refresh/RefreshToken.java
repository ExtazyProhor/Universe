package ru.prohor.universe.scarif.services.refresh;

import org.bson.types.ObjectId;

public record RefreshToken(
        ObjectId userId,
        ObjectId sessionId,
        ObjectId refreshTokenId
) {
    public static final String REFRESH_TOKEN_ATTRIBUTE_KEY = "scarif.refresh-token";
}
