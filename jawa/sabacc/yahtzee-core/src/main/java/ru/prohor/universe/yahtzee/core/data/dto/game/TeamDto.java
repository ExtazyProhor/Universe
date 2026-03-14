package ru.prohor.universe.yahtzee.core.data.dto.game;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TeamDto {
    private List<ObjectId> players;
    @Nullable
    private List<ScoreDto> scores;
    private int total;
    @Nullable
    @Property("has_bonus")
    private Boolean hasBonus;
    @Nullable
    private String title;
    @Nullable
    private Integer color;
}
