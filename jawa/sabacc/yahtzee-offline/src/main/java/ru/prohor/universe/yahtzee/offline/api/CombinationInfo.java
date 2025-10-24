package ru.prohor.universe.yahtzee.offline.api;

import ru.prohor.universe.yahtzee.core.core.Combination;

public record CombinationInfo(
        Combination combination,
        int value
) {}
