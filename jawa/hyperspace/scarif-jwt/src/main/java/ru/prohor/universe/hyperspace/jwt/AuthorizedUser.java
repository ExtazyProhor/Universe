package ru.prohor.universe.hyperspace.jwt;

import java.util.UUID;

public record AuthorizedUser(
        long id,
        UUID uuid,
        String objectId,
        String username
) {
    public static final String AUTHORIZED_USER_ATTRIBUTE_KEY = "universe.authorized-user";
}
