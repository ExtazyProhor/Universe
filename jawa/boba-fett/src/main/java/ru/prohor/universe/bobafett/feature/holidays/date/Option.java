package ru.prohor.universe.bobafett.feature.holidays.date;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Option {
    @JsonProperty("a")
    APPLY,
    @JsonProperty("b")
    CHANGE_DATE,
    @JsonProperty("c")
    CANCEL
}
