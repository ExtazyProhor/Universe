package ru.prohor.universe.yahtzee.app.web.api;

public record PlayerInfo(
        String id,
        String name,
        boolean moving // next move of his team is up to this player
) {}
