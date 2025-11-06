package ru.prohor.universe.venator.webhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitInfo(@NotNull String message) {}
