package ru.prohor.universe.yahtzee.app.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.prohor.universe.yahtzee.core.data.Combination;

public record SaveMoveRequest(
        @JsonProperty("moving_player_id")
        String movingPlayerId,
        Combination combination,
        int value
) {}
