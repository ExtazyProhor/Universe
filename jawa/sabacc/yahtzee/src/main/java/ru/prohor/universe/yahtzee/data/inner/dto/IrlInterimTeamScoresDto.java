package ru.prohor.universe.yahtzee.data.inner.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Entity
public class IrlInterimTeamScoresDto {
    @Property("moving_player_index")
    private int movingPlayerIndex;
    private String title;
    private int color;
    private List<ObjectId> players;
    private List<IrlScoreDto> scores;

    @SuppressWarnings("unused")
    public IrlInterimTeamScoresDto() {}

    public IrlInterimTeamScoresDto(
            int movingPlayerIndex,
            String title,
            int color,
            List<ObjectId> players,
            List<IrlScoreDto> scores
    ) {
        this.movingPlayerIndex = movingPlayerIndex;
        this.title = title;
        this.color = color;
        this.players = players;
        this.scores = scores;
    }
}
