package ru.prohor.universe.yahtzee.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.data.inner.dto.OfflineTeamScoresDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("offline_games")
public class OfflineGameDto {
    @Id
    private ObjectId id;
    private LocalDate date;
    @Property("finish_time")
    private LocalTime finishTime;
    private ObjectId initiator;
    private List<OfflineTeamScoresDto> teams;
}
