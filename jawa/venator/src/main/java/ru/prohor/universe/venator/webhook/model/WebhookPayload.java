package ru.prohor.universe.venator.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record WebhookPayload(
        @NotNull
        String ref,
        @NotNull
        RepositoryInfo repository,
        @NotNull
        SenderInfo sender,
        @NotNull
        @JsonProperty("head_commit")
        CommitInfo headCommit
) {}
