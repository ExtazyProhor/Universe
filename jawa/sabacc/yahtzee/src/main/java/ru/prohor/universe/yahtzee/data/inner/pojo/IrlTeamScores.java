package ru.prohor.universe.yahtzee.data.inner.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.inner.dto.IrlTeamScoresDto;

import java.util.List;

public record IrlTeamScores(
        List<ObjectId> players,
        Opt<List<IrlScore>> scores,
        int total,
        Opt<Boolean> hasBonus
) implements MongoEntityPojo<IrlTeamScoresDto> {
    @Override
    public IrlTeamScoresDto toDto() {
        return new IrlTeamScoresDto(
                players,
                scores.map(s -> s.stream().map(IrlScore::toDto).toList()).orElseNull(),
                total,
                hasBonus.orElseNull()
        );
    }

    public static IrlTeamScores fromDto(IrlTeamScoresDto team) {
        return new IrlTeamScores(
                team.getPlayers(),
                Opt.ofNullable(team.getScores()).map(s -> s.stream().map(IrlScore::fromDto).toList()),
                team.getTotal(),
                Opt.ofNullable(team.hasBonus())
        );
    }
}
