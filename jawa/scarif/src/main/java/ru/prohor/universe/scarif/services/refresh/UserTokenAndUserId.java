package ru.prohor.universe.scarif.services.refresh;

public record UserTokenAndUserId(
        long userId,
        String userToken
) {}
