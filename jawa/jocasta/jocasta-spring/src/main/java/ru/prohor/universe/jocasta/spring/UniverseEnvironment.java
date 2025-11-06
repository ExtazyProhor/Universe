package ru.prohor.universe.jocasta.spring;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum UniverseEnvironment {
    LOCAL,
    STABLE,
    TESTING;

    private static final Map<String, UniverseEnvironment> MAPPER = Arrays.stream(values())
            .collect(Collectors.toMap(env -> env.name().toLowerCase(), MonoFunction.identity()));

    @Nonnull
    public static UniverseEnvironment get(@Nonnull String environmentName) {
        UniverseEnvironment env = MAPPER.get(environmentName.toLowerCase());
        if (env == null)
            throw new IllegalArgumentException("Illegal UniverseEnvironment name");
        return env;
    }
}
