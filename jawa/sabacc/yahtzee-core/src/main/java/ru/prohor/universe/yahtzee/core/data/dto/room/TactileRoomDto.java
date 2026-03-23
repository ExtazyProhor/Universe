package ru.prohor.universe.yahtzee.core.data.dto.room;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("tactile_rooms")
public class TactileRoomDto {
    @Id
    private ObjectId id;
    @Property("created_at")
    private Instant createdAt;
    private ObjectId initiator;
    @Property("moving_team_index")
    private int movingTeamIndex;
    private List<TactileIntermediateTeamDto> teams;
}
