package ru.prohor.universe.yahtzee.core.data.dto.game;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VirtualGamePropertiesDto extends GamePropertiesDto {
    @Property("total_rerolls")
    private int totalRerolls;
}
