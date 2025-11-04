package ru.prohor.universe.venator.old;

import java.nio.file.Path;
import java.time.Instant;

public record ServiceInfo(
        String name,
        Path jarPath,
        long pid,
        Instant startedAt,
        boolean alive)
{}
