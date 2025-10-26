package ru.prohor.universe.yahtzee.offline.api;

import ru.prohor.universe.yahtzee.core.core.TeamColor;

import java.util.List;

public record TeamInfo(
        String title,
        TeamColor color,
        boolean moving, // next move is up to this team
        List<PlayerInfo> players,
        List<CombinationInfo> results // combinations that already filled
) {}
