package ru.prohor.universe.yahtzee.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.data.inner.dto.IrlInterimTeamScoresDto;

import java.time.Instant;
import java.util.List;

@Entity("irl_rooms")
public class IrlRoomDto {
    @Id
    private ObjectId id;
    @Property("created_at")
    private Instant createdAt;
    private ObjectId initiator;
    private List<IrlInterimTeamScoresDto> teams;

    public IrlRoomDto() {}

    public IrlRoomDto(
            ObjectId id,
            Instant createdAt,
            ObjectId initiator,
            List<IrlInterimTeamScoresDto> teams
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.initiator = initiator;
        this.teams = teams;
    }

    public ObjectId getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ObjectId getInitiator() {
        return initiator;
    }

    public List<IrlInterimTeamScoresDto> getTeams() {
        return teams;
    }
}
