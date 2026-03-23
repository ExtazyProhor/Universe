package ru.prohor.universe.yahtzee.core.data.dto.game;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.prohor.universe.yahtzee.core.data.TactileGameSource;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TactileGamePropertiesDto extends GamePropertiesDto {
    private TactileGameSource source;
}
