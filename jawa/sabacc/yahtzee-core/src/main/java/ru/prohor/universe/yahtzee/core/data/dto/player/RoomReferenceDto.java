package ru.prohor.universe.yahtzee.core.data.dto.player;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.core.data.GameType;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RoomReferenceDto {
    private ObjectId id;
    private GameType type;
    @Property("created_at")
    private Instant createdAt;
    @Property("teams_count")
    private int teamsCount;
    @Property("players_count")
    private int playersCount;
}
