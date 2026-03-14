package ru.prohor.universe.yahtzee.core.data.pojo.room;

import lombok.Builder;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.data.dto.room.TactileIntermediateTeamDto;
import ru.prohor.universe.yahtzee.core.data.pojo.game.TactileScore;

import java.util.List;

@Builder(toBuilder = true)
public record TactileIntermediateTeam(
        List<ObjectId> players,
        List<TactileScore> scores,
        int movingPlayerIndex,
        String title,
        int color
) implements MongoEntityPojo<TactileIntermediateTeamDto> {
    @Override
    public TactileIntermediateTeamDto toDto() {
        return new TactileIntermediateTeamDto(
                players,
                scores.stream().map(TactileScore::toTypedDto).toList(),
                movingPlayerIndex,
                title,
                color
        );
    }

    public static TactileIntermediateTeam fromDto(TactileIntermediateTeamDto dto) {
        return new TactileIntermediateTeam(
                dto.getPlayers(),
                dto.getScores().stream().map(TactileScore::fromDto).toList(),
                dto.getMovingPlayerIndex(),
                dto.getTitle(),
                dto.getColor()
        );
    }
}
