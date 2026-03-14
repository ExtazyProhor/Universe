package ru.prohor.universe.yahtzee.app.web.api;

import java.util.List;

public record RoomInfoResponse(
        List<TeamInfo> teams
) {}
