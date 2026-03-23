package ru.prohor.universe.yahtzee.core.data.pojo.game;

import ru.prohor.universe.yahtzee.core.data.Combination;
import ru.prohor.universe.yahtzee.core.data.dto.game.ScoreDto;

public interface Score {
    ScoreDto toDto();

    Combination combination();

    int value();
}
