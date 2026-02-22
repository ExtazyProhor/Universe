package ru.prohor.universe.bobafett.feature.holidays.date;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TimeUnit {
    @JsonProperty("a")
    DAY,
    @JsonProperty("b")
    MONTH
}
