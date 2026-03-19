package ru.prohor.universe.yahtzee.app.web.api;

import ru.prohor.universe.yahtzee.core.data.GameType;

public record GeneralRoomInfo(
        GameType type,
        String date,
        int teams,
        int players
) {}
