package ru.prohor.universe.scarif.data;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

public record ExternalAccount(
        String id,
        ExternalAccountProvider provider,
        boolean primary,
        Opt<String> email,
        Opt<String> name,
        Opt<String> givenName,
        Opt<String> familyName,
        Opt<String> pictureUrl,
        Opt<String> locale
) {}
