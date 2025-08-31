package ru.prohor.universe.yahtzee.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.data.inner.dto.IrlInterimTeamScores;

import java.time.Instant;
import java.util.List;

@Entity("irl_rooms")
public class IrlRoomDto {
    @Id
    private ObjectId id;
    @Property("created_at")
    private Instant createdAt;
    private ObjectId initiator;
    private List<IrlInterimTeamScores> teams;

    public IrlRoomDto() {}
}
