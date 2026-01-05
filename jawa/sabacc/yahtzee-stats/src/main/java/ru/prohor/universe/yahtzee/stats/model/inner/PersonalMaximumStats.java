package ru.prohor.universe.yahtzee.stats.model.inner;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PersonalMaximumStats {
    private ObjectId player;
    private int value;
    private Instant date;
}
