package ru.prohor.universe.yahtzee.core.data.pojo.game;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.data.dto.game.ScoreDto;
import ru.prohor.universe.yahtzee.core.data.dto.game.TeamDto;

import java.util.List;

public record Team<TScore extends Score>(
        List<ObjectId> players,
        Opt<List<TScore>> scores,
        int total,
        Opt<Boolean> hasBonus,
        Opt<Integer> color,
        Opt<Integer> index
) implements MongoEntityPojo<TeamDto> {
    @Override
    public TeamDto toDto() {
        return new TeamDto(
                players,
                scores.map(scores -> scores.stream().map(Score::toDto).toList()).orElseNull(),
                total,
                hasBonus.orElseNull(),
                color.orElseNull(),
                index.orElseNull()
        );
    }

    public static <TScore extends Score> Team<TScore> fromDto(TeamDto dto, MonoFunction<ScoreDto, TScore> mapper) {
        return new Team<>(
                dto.getPlayers(),
                Opt.ofNullable(dto.getScores()).map(scores -> scores.stream().map(mapper).toList()),
                dto.getTotal(),
                Opt.ofNullable(dto.getHasBonus()),
                Opt.ofNullable(dto.getColor()),
                Opt.ofNullable(dto.getIndex())
        );
    }
}
