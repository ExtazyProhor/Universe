package ru.prohor.universe.yahtzee.core.data.pojo.room;

import lombok.Builder;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.data.dto.room.TactileRoomDto;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record TactileRoom(
        ObjectId id,
        Instant createdAt,
        ObjectId initiator,
        int movingTeamIndex,
        List<TactileIntermediateTeam> teams
) implements MongoEntityPojo<TactileRoomDto> {
    @Override
    public TactileRoomDto toDto() {
        return new TactileRoomDto(
                id,
                createdAt,
                initiator,
                movingTeamIndex,
                teams.stream().map(TactileIntermediateTeam::toDto).toList()
        );
    }

    public static TactileRoom fromDto(TactileRoomDto dto) {
        return new TactileRoom(
                dto.getId(),
                dto.getCreatedAt(),
                dto.getInitiator(),
                dto.getMovingTeamIndex(),
                dto.getTeams().stream().map(TactileIntermediateTeam::fromDto).toList()
        );
    }
}
