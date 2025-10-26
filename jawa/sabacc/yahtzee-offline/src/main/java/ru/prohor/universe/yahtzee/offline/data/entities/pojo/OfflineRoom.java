package ru.prohor.universe.yahtzee.offline.data.entities.pojo;

import lombok.Builder;
import org.bson.types.ObjectId;
import org.joda.time.Instant;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.core.GameRoom;
import ru.prohor.universe.yahtzee.core.core.RoomType;
import ru.prohor.universe.yahtzee.offline.data.entities.dto.OfflineRoomDto;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineInterimTeamScores;

import java.util.List;

@Builder(toBuilder = true)
public record OfflineRoom(
        ObjectId id,
        Instant createdAt,
        ObjectId initiator,
        int movingTeamIndex,
        List<OfflineInterimTeamScores> teams
) implements MongoEntityPojo<OfflineRoomDto>, GameRoom {
    @Override
    public OfflineRoomDto toDto() {
        return new OfflineRoomDto(
                id,
                DateTimeUtil.unwrap(createdAt),
                initiator,
                movingTeamIndex,
                teams.stream().map(OfflineInterimTeamScores::toDto).toList()
        );
    }

    public static OfflineRoom fromDto(OfflineRoomDto room) {
        return new OfflineRoom(
                room.getId(),
                DateTimeUtil.wrap(room.getCreatedAt()),
                room.getInitiator(),
                room.getMovingTeamIndex(),
                room.getTeams().stream().map(OfflineInterimTeamScores::fromDto).toList()
        );
    }

    @Override
    public int teamsCount() {
        return teams.size();
    }

    @Override
    public RoomType type() {
        return RoomType.TACTILE_OFFLINE;
    }
}
