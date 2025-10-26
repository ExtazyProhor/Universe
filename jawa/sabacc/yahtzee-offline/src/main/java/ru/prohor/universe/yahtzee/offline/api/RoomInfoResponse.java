package ru.prohor.universe.yahtzee.offline.api;

import java.util.List;

public record RoomInfoResponse(
        List<TeamInfo> teams
) {}
