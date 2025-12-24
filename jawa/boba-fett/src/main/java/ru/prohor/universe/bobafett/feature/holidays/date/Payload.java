package ru.prohor.universe.bobafett.feature.holidays.date;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDate;

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
        Boolean increase
) {}
