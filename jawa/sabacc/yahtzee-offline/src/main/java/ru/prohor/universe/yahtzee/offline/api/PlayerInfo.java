package ru.prohor.universe.yahtzee.offline.api;

public record PlayerInfo(
        String id,
        String name,
        boolean moving // next move of his team is up to this player
) {}
