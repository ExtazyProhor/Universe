package ru.prohor.universe.yahtzee.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.data.inner.dto.IrlTeamScoresDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity("irl_games")
public class IrlGameDto {
    @Id
    private ObjectId id;
    private LocalDate date;
    @Property("finish_time")
    private LocalTime finishTime;
    private ObjectId initiator;
    private List<IrlTeamScoresDto> teams;

    public IrlGameDto() {}

    public IrlGameDto(
            ObjectId id,
            LocalDate date,
            LocalTime finishTime,
            ObjectId initiator,
            List<IrlTeamScoresDto> teams
    ) {
        this.id = id;
        this.date = date;
        this.finishTime = finishTime;
        this.initiator = initiator;
        this.teams = teams;
    }

    public ObjectId getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getFinishTime() {
        return finishTime;
    }

    public ObjectId getInitiator() {
        return initiator;
    }

    public List<IrlTeamScoresDto> getTeams() {
        return teams;
    }
}
