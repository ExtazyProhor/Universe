package ru.prohor.universe.yahtzee.core.data.dto.game;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.core.data.Combination;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VirtualScoreDto extends ScoreDto {
    private Combination combination;
    private int value;
    private ObjectId thrower;
    private List<VirtualRollDto> rolls;
    private int round;
    private int rerolls;
}
