package ru.prohor.universe.yahtzee.stats.model.inner;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GamesCountStats {
    private ObjectId player;
    private int value;
}
