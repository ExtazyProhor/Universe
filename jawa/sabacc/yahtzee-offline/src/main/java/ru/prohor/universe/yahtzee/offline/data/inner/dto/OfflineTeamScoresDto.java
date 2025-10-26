package ru.prohor.universe.yahtzee.offline.data.inner.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OfflineTeamScoresDto {
    @Getter
    private List<ObjectId> players;
    @Getter
    private List<OfflineScoreDto> scores;
    @Getter
    private int total;
    @Property("has_bonus")
    private Boolean hasBonus;

    public Boolean hasBonus() {
        return hasBonus;
    }
}
