package ru.prohor.universe.venator.webhook;

import jakarta.validation.constraints.NotNull;

public record WebhookPayload(
        @NotNull
        String ref,
        @NotNull
        RepositoryInfo repository
) {}
