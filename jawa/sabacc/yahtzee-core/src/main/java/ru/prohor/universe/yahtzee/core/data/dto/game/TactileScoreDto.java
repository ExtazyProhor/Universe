package ru.prohor.universe.yahtzee.core.data.dto.game;

import dev.morphia.annotations.Entity;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.core.data.Combination;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TactileScoreDto extends ScoreDto {
    private Combination combination;
    private int value;
    @Nullable
    private ObjectId thrower;
    @Nullable
    private Integer round;
}
