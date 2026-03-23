package ru.prohor.universe.yahtzee.core.data.pojo.game;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.utils.CastUtils;
import ru.prohor.universe.yahtzee.core.data.Combination;
import ru.prohor.universe.yahtzee.core.data.dto.game.ScoreDto;
import ru.prohor.universe.yahtzee.core.data.dto.game.VirtualRollDto;
import ru.prohor.universe.yahtzee.core.data.dto.game.VirtualScoreDto;

import java.util.List;

public record VirtualScore(
        Combination combination,
        int value,
        ObjectId thrower,
        List<VirtualRollDto> rolls,
        int round,
        int rerolls
) implements Score {
    @Override
    public ScoreDto toDto() {
        return toTypedDto();
    }

    public VirtualScoreDto toTypedDto() {
        return new VirtualScoreDto(
                combination,
                value,
                thrower,
                rolls,
                round,
                rerolls
        );
    }

    public static VirtualScore fromDto(ScoreDto scoreDto) {
        VirtualScoreDto dto = CastUtils.cast(scoreDto);
        return new VirtualScore(
                dto.getCombination(),
                dto.getValue(),
                dto.getThrower(),
                dto.getRolls(),
                dto.getRound(),
                dto.getRerolls()
        );
    }
}
