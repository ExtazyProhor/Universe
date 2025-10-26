package ru.prohor.universe.yahtzee.offline.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TeamPlayers(
        @NotNull
        @Size(min = 2, max = 20)
        String title,
        @JsonProperty("players_ids")
        List<String> playersIds
) {}
