package ru.prohor.universe.yahtzee.core.data.dto.game;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VirtualRollDto {
    @Property("reroll_index")
    private int rerollIndex;
    private List<VirtualDiceDto> dice;
}
