package ru.prohor.universe.yahtzee.offline.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SaveMoveResponse(
        @JsonProperty("next_move_player_id")
        String nextMovePlayerId,
        @JsonProperty("game_over") // if true - next_move_player_id is absent, show dialog window
        boolean gameOver
) {}
