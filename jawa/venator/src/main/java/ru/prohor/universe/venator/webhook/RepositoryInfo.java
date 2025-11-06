package ru.prohor.universe.venator.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record RepositoryInfo(
        @NotNull
        @JsonProperty("full_name")
        String fullName,
        @NotNull
        @JsonProperty("clone_url")
        String cloneUrl
) {}
