package ru.prohor.universe.yahtzee.offline.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateRoomRequest(
        @NotNull
        @Size(min = 1, max = 8)
        List<TeamPlayers> teams
) {}
