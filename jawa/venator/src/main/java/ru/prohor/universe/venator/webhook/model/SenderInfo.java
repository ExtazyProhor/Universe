package ru.prohor.universe.venator.webhook.model;

import jakarta.validation.constraints.NotNull;

public record SenderInfo(@NotNull String login) {}
