package ru.prohor.universe.yahtzee.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TeamColor(
        @JsonIgnore
        int colorId, // internal usage
        @JsonProperty("bg")
        String background,
        String text,
        String light,
        String dark
) {}
