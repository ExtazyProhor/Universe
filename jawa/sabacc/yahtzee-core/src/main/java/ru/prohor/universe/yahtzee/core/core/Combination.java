package ru.prohor.universe.yahtzee.core.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Combination {
    @JsonProperty("units")
    UNITS,
    @JsonProperty("twos")
    TWOS,
    @JsonProperty("threes")
    THREES,
    @JsonProperty("fours")
    FOURS,
    @JsonProperty("fives")
    FIVES,
    @JsonProperty("sixes")
    SIXES,

    @JsonProperty("pair")
    PAIR,
    @JsonProperty("two_pairs")
    TWO_PAIRS,
    @JsonProperty("three_of_kind")
    THREE_OF_KIND,
    @JsonProperty("four_of_kind")
    FOUR_OF_KIND,
    @JsonProperty("full_house")
    FULL_HOUSE,
    @JsonProperty("low_straight")
    LOW_STRAIGHT,
    @JsonProperty("high_straight")
    HIGH_STRAIGHT,
    @JsonProperty("yahtzee")
    YAHTZEE,
    @JsonProperty("chance")
    CHANCE;

    public String propertyName() {
        return name().toLowerCase();
    }

    public static Combination of(String propertyName) {
        return valueOf(propertyName.toUpperCase());
    }
}
