package ru.prohor.universe.yahtzee.core.core;

import org.joda.time.Instant;

public interface GameRoom {
    Instant createdAt();

    int teamsCount();

    RoomType type();
}
