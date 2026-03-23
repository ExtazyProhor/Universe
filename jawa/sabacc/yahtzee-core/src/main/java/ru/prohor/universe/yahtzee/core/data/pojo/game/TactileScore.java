package ru.prohor.universe.yahtzee.core.data.pojo.game;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.utils.CastUtils;
import ru.prohor.universe.yahtzee.core.data.Combination;
import ru.prohor.universe.yahtzee.core.data.dto.game.ScoreDto;
import ru.prohor.universe.yahtzee.core.data.dto.game.TactileScoreDto;

public record TactileScore(
        Combination combination,
        int value,
        Opt<ObjectId> thrower,
        Opt<Integer> round
) implements Score {
    @Override
    public ScoreDto toDto() {
        return toTypedDto();
    }

    public TactileScoreDto toTypedDto() {
        return new TactileScoreDto(
                combination,
                value,
                thrower.orElseNull(),
                round.orElseNull()
        );
    }

    public static TactileScore fromDto(ScoreDto scoreDto) {
        TactileScoreDto dto = CastUtils.cast(scoreDto);
        return new TactileScore(
                dto.getCombination(),
                dto.getValue(),
                Opt.ofNullable(dto.getThrower()),
                Opt.ofNullable(dto.getRound())
        );
    }
}
