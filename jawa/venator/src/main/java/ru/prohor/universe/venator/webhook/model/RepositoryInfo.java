package ru.prohor.universe.venator.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record RepositoryInfo(
        @NotNull @JsonProperty("full_name")
        String fullName,
        @NotNull @JsonProperty("pushed_at")
        long pushedAt,
        @NotNull @JsonProperty("clone_url")
        String cloneUrl,
        @NotNull @JsonProperty("master_branch")
        String masterBranch
) {}
