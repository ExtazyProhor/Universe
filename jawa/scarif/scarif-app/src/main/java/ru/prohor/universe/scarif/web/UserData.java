package ru.prohor.universe.scarif.web;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

public record UserData(
        String ip,
        Opt<String> userAgent
) {}
