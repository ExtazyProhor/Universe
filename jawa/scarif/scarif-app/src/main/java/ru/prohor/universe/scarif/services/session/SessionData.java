package ru.prohor.universe.scarif.services.session;

import ru.prohor.universe.scarif.data.Session;

public record SessionData(
        Session session,
        String refreshJwt,
        String accessJwt
) {}
