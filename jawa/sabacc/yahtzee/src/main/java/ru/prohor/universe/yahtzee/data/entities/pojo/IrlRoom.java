package ru.prohor.universe.yahtzee.data.entities.pojo;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import ru.prohor.universe.jocasta.jodaTime.DateTimeUtil;
import ru.prohor.universe.yahtzee.data.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.entities.dto.IrlRoomDto;
import ru.prohor.universe.yahtzee.data.inner.pojo.IrlInterimTeamScores;

import java.util.List;

public record IrlRoom(
        ObjectId id,
        Instant createdAt,
        ObjectId initiator,
        List<IrlInterimTeamScores> teams
) implements MongoEntityPojo<IrlRoomDto> {
    @Override
    public IrlRoomDto toDto() {
        return new IrlRoomDto(
                id,
                DateTimeUtil.unwrap(createdAt),
                initiator,
                teams.stream().map(IrlInterimTeamScores::toDto).toList()
        );
    }

    public static IrlRoom fromDto(IrlRoomDto room) {
        return new IrlRoom(
                room.getId(),
                DateTimeUtil.wrap(room.getCreatedAt()),
                room.getInitiator(),
                room.getTeams().stream().map(IrlInterimTeamScores::fromDto).toList()
        );
    }
}
