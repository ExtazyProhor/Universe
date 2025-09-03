package ru.prohor.universe.yahtzee.data.inner.pojo;

import lombok.Builder;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.data.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.inner.dto.IrlInterimTeamScoresDto;

import java.util.List;

@Builder(toBuilder = true)
public record IrlInterimTeamScores(
        int movingPlayerIndex,
        String title,
        int color,
        List<ObjectId> players,
        List<IrlScore> scores
) implements MongoEntityPojo<IrlInterimTeamScoresDto> {
    @Override
    public IrlInterimTeamScoresDto toDto() {
        return new IrlInterimTeamScoresDto(
                movingPlayerIndex,
                title,
                color,
                players,
                scores.stream().map(IrlScore::toDto).toList()
        );
    }

    public static IrlInterimTeamScores fromDto(IrlInterimTeamScoresDto team) {
        return new IrlInterimTeamScores(
                team.getMovingPlayerIndex(),
                team.getTitle(),
                team.getColor(),
                team.getPlayers(),
                team.getScores().stream().map(IrlScore::fromDto).toList()
        );
    }
}
