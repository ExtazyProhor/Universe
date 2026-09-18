package ru.prohor.universe.scarif.jwt;

import java.util.UUID;

public record AuthorizedUser(
        long numericId,
        UUID uuid,
        String objectId
) {
    public static final String AUTHORIZED_USER_ATTRIBUTE_KEY = "universe.authorized-user";
}
