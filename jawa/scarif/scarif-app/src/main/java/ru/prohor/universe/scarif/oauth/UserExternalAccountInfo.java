package ru.prohor.universe.scarif.oauth;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

public record UserExternalAccountInfo(
        String id,
        Opt<String> email,
        Opt<String> name,
        Opt<String> givenName,
        Opt<String> familyName,
        Opt<String> pictureUrl,
        Opt<String> locale
) {}
