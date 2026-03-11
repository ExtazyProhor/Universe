package ru.prohor.universe.yahtzee.offline.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.core.data.dto.RoomReferenceDto;
import ru.prohor.universe.yahtzee.offline.data.inner.TactileGameSource;
import ru.prohor.universe.yahtzee.offline.data.inner.dto.OfflineTeamScoresDto;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("games")
public class OfflineGameDto {
    @Id
    private ObjectId id;
    private Instant date;
    private ObjectId initiator;
    private List<OfflineTeamScoresDto> teams;
    private boolean trusted;
    private TactileGameSource source;
    private RoomReferenceDto room;
}
