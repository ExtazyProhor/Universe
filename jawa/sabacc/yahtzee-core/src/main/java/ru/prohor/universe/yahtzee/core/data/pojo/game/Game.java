package ru.prohor.universe.yahtzee.core.data.pojo.game;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.features.fieldref.Name;
import ru.prohor.universe.jocasta.core.utils.CastUtils;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.data.GameType;
import ru.prohor.universe.yahtzee.core.data.dto.game.GameDto;

import java.time.Instant;
import java.util.List;

public interface Game extends MongoEntityPojo<GameDto> {
    ObjectId id();

    List<ObjectId> players();

    Instant date();

    ObjectId initiator();

    boolean trusted();

    GameType type();

    @Name("teams")
    List<Team<Score>> getTeams();

    default TactileGame tactile() {
        if (type().isTactile())
            return CastUtils.cast(this);
        throw new IllegalStateException("Game is not tactile, type=" + type());
    }

    default VirtualGame virtual() {
        if (type().isVirtual())
            return CastUtils.cast(this);
        throw new IllegalStateException("Game is not virtual, type=" + type());
    }

    static Game fromDto(GameDto dto) {
        return switch (dto.getType()) {
            case TACTILE_OFFLINE -> TactileGame.fromDto(dto);
            case VIRTUAL_ONLINE, VIRTUAL_OFFLINE -> VirtualGame.fromDto(dto);
        };
    }
}
