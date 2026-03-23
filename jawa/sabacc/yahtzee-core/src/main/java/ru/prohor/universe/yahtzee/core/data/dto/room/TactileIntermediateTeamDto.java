package ru.prohor.universe.yahtzee.core.data.dto.room;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.core.data.dto.game.TactileScoreDto;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TactileIntermediateTeamDto {
    private List<ObjectId> players;
    private List<TactileScoreDto> scores;
    @Property("moving_player_index")
    private int movingPlayerIndex;
    private String title;
    private int color;
}
