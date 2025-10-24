package ru.prohor.universe.yahtzee.offline.data.inner.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OfflineInterimTeamScoresDto {
    @Property("moving_player_index")
    private int movingPlayerIndex;
    private String title;
    private int color;
    private List<ObjectId> players;
    private List<OfflineScoreDto> scores;
}
