package ru.prohor.universe.yahtzee.core.data.pojo.game;

import ru.prohor.universe.jocasta.core.utils.CastUtils;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.data.dto.game.GameDto;

public interface Game extends MongoEntityPojo<GameDto> {
    default TactileGame tactile() {
        return CastUtils.cast(this);
    }

    default VirtualGame virtual() {
        return CastUtils.cast(this);
    }

    static Game fromDto(GameDto dto) {
        return switch (dto.getType()) {
            case TACTILE_OFFLINE -> TactileGame.fromDto(dto);
            case VIRTUAL_ONLINE, VIRTUAL_OFFLINE -> VirtualGame.fromDto(dto);
        };
    }
}
