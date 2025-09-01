package ru.prohor.universe.yahtzee.data.inner.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.data.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.inner.dto.IrlInterimTeamScoresDto;

import java.util.List;

public record IrlInterimTeamScores(
        List<ObjectId> players,
        List<IrlScore> scores
) implements MongoEntityPojo<IrlInterimTeamScoresDto>  {
    @Override
    public IrlInterimTeamScoresDto toDto() {
        return new IrlInterimTeamScoresDto(
                players,
                scores.stream().map(IrlScore::toDto).toList()
        );
    }

    public static IrlInterimTeamScores fromDto(IrlInterimTeamScoresDto team) {
        return new IrlInterimTeamScores(
                team.getPlayers(),
                team.getScores().stream().map(IrlScore::fromDto).toList()
        );
    }
}
