package ru.prohor.universe.venator.webhook.model;

import jakarta.validation.constraints.NotNull;

public record CommitInfo(@NotNull String message) {}
