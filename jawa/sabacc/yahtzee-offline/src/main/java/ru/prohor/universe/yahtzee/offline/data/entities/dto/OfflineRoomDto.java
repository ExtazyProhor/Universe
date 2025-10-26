package ru.prohor.universe.yahtzee.offline.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.offline.data.inner.dto.OfflineInterimTeamScoresDto;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("offline_rooms")
public class OfflineRoomDto {
    @Id
    private ObjectId id;
    @Property("created_at")
    private Instant createdAt;
    private ObjectId initiator;
    @Property("moving_team_index")
    private int movingTeamIndex;
    private List<OfflineInterimTeamScoresDto> teams;
}
