package ru.prohor.universe.yahtzee.core.core;

import java.time.Instant;

public interface GameRoom {
    Instant createdAt();

    int teamsCount();

    RoomType type();
}
