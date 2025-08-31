package ru.prohor.universe.yahtzee.data.inner.dto;

import dev.morphia.annotations.Entity;

@Entity
public class IrlScoreDto {
    private String combination;
    private int value;

    public IrlScoreDto() {}

    public IrlScoreDto(String combination, int value) {
        this.combination = combination;
        this.value = value;
    }

    public String getCombination() {
        return combination;
    }

    public int getValue() {
        return value;
    }
}
