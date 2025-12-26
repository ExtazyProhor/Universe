package ru.prohor.universe.bobafett.feature.holidays.date;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDate;
import ru.prohor.universe.jocasta.core.collections.common.Bool;

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
