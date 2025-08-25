package ru.prohor.universe.scarif.services.refresh;

import ru.prohor.universe.scarif.data.refresh.RefreshToken;

import java.util.function.Predicate;

public record ParsedUserToken(
        long id,
        Predicate<RefreshToken> tokenValidator
) {}
