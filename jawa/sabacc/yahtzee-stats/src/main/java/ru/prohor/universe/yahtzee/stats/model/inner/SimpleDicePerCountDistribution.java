package ru.prohor.universe.yahtzee.stats.model.inner;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SimpleDicePerCountDistribution {
    private int dice;
    private int count;
    private float percent;
}
