package ru.prohor.universe.yahtzee.app.web.api;

import ru.prohor.universe.yahtzee.core.data.Combination;

public record CombinationInfo(
        Combination combination,
        int value
) {}
