package ru.prohor.universe.yahtzee.data.inner.pojo;

import lombok.Builder;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.inner.dto.OfflineInterimTeamScoresDto;

import java.util.List;

@Builder(toBuilder = true)
public record OfflineInterimTeamScores(
        int movingPlayerIndex,
        String title,
        int color,
        List<ObjectId> players,
        List<OfflineScore> scores
) implements MongoEntityPojo<OfflineInterimTeamScoresDto> {
    @Override
    public OfflineInterimTeamScoresDto toDto() {
        return new OfflineInterimTeamScoresDto(
                movingPlayerIndex,
                title,
                color,
                players,
                scores.stream().map(OfflineScore::toDto).toList()
        );
    }

    public static OfflineInterimTeamScores fromDto(OfflineInterimTeamScoresDto team) {
        return new OfflineInterimTeamScores(
                team.getMovingPlayerIndex(),
                team.getTitle(),
                team.getColor(),
                team.getPlayers(),
                team.getScores().stream().map(OfflineScore::fromDto).toList()
        );
    }
}
