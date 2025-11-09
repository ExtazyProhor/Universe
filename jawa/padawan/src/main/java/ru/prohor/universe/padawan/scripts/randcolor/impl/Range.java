package ru.prohor.universe.padawan.scripts.randcolor.impl;

public record Range(
        int start,
        int end
) {
    public boolean contain(int value) {
        return value >= start && value <= end;
    }
}
