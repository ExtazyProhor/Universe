package ru.prohor.universe.bobafett.feature.holidays.date;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.prohor.universe.jocasta.core.collections.common.Bool;

import java.time.LocalDate;

public record Payload(
        @JsonProperty("a")
        Option option,
        @JsonProperty("b")
        LocalDate date,
        @JsonProperty("c")
        TimeUnit timeUnit,
        @JsonProperty("d")
        Integer count,
        @JsonProperty("e")
        Bool increase
) {}
