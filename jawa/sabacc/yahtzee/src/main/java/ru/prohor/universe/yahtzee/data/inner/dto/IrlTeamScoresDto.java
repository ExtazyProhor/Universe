package ru.prohor.universe.yahtzee.data.inner.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;

import java.util.List;

@Entity
public class IrlTeamScoresDto {
    private List<ObjectId> players;
    private List<IrlScoreDto> scores;
    private int total;
    @Property("has_bonus")
    private Boolean hasBonus;

    public IrlTeamScoresDto() {}

    public IrlTeamScoresDto(
            List<ObjectId> players,
            List<IrlScoreDto> scores,
            int total,
            Boolean hasBonus
    ) {
        this.players = players;
        this.scores = scores;
        this.total = total;
        this.hasBonus = hasBonus;
    }

    public List<ObjectId> getPlayers() {
        return players;
    }

    public List<IrlScoreDto> getScores() {
        return scores;
    }

    public int getTotal() {
        return total;
    }

    public Boolean hasBonus() {
        return hasBonus;
    }
}
