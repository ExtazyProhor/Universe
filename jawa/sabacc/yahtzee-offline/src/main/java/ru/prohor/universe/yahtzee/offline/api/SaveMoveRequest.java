package ru.prohor.universe.yahtzee.offline.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.prohor.universe.yahtzee.core.Combination;

public record SaveMoveRequest(
        @JsonProperty("moving_player_id")
        String movingPlayerId,
        Combination combination,
        int value
) {}
