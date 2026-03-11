package ru.prohor.universe.yahtzee.core.data.dto.game;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.core.data.GameType;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("games")
public class GameDto {
    @Id
    private ObjectId id;
    private List<ObjectId> players;
    private Instant date;
    private ObjectId initiator;
    private List<TeamDto> teams;
    private boolean trusted;
    @Nullable
    private ObjectId room;
    private GameType type;
    private GamePropertiesDto properties;
}
