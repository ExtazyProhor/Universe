package ru.prohor.universe.yahtzee.stats.model.inner;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.prohor.universe.yahtzee.core.core.Combination;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FloatCombinationStats {
    private Combination combination;
    private float value;
}
