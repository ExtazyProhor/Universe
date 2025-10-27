package ru.prohor.universe.yahtzee.offline.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.prohor.universe.yahtzee.core.core.color.TeamColor;

import java.util.List;

public record TeamInfo(
        String title,
        @JsonProperty("random_сolor")
        boolean randomColor, // if present - color is absent
        TeamColor color,
        boolean moving, // next move is up to this team
        List<PlayerInfo> players,
        List<CombinationInfo> results // combinations that already filled
) {}
