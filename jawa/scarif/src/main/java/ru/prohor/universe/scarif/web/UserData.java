package ru.prohor.universe.scarif.web;

import ru.prohor.universe.jocasta.core.collections.Opt;

public record UserData(
        Opt<String> ip,
        Opt<String> userAgent
) {}
