package ru.prohor.universe.yahtzee.data.inner.dto;

import dev.morphia.annotations.Entity;
import org.bson.types.ObjectId;

import java.util.List;

@Entity
public class IrlInterimTeamScoresDto {
    private List<ObjectId> players;
    private List<IrlScoreDto> scores;

    public IrlInterimTeamScoresDto() {}

    public IrlInterimTeamScoresDto(
            List<ObjectId> players,
            List<IrlScoreDto> scores
    ) {
        this.players = players;
        this.scores = scores;
    }

    public List<ObjectId> getPlayers() {
        return players;
    }

    public List<IrlScoreDto> getScores() {
        return scores;
    }
}
