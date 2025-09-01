package ru.prohor.universe.yahtzee.data.inner.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;

import java.util.List;

@Entity
public class IrlInterimTeamScoresDto {
    @Property("team_id")
    private int teamId;
    @Property("moving_player_index")
    private int movingPlayerIndex;
    private List<ObjectId> players;
    private List<IrlScoreDto> scores;

    public IrlInterimTeamScoresDto() {}

    public IrlInterimTeamScoresDto(
            int teamId,
            int movingPlayerIndex,
            List<ObjectId> players,
            List<IrlScoreDto> scores
    ) {
        this.teamId = teamId;
        this.movingPlayerIndex = movingPlayerIndex;
        this.players = players;
        this.scores = scores;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getMovingPlayerIndex() {
        return movingPlayerIndex;
    }

    public List<ObjectId> getPlayers() {
        return players;
    }

    public List<IrlScoreDto> getScores() {
        return scores;
    }
}
