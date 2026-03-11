package ru.prohor.universe.yahtzee.core.data.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.core.GameType;
import ru.prohor.universe.yahtzee.core.data.dto.RoomReferenceDto;

public record RoomReference(
        ObjectId id,
        GameType type
) implements MongoEntityPojo<RoomReferenceDto> {
    @Override
    public RoomReferenceDto toDto() {
        return new RoomReferenceDto(id, type);
    }

    public static RoomReference fromDto(RoomReferenceDto roomRef) {
        return new RoomReference(roomRef.getId(), roomRef.getType());
    }

    public boolean isTactileOffline() {
        return type == GameType.TACTILE_OFFLINE;
    }

    public boolean isVirtualOnline() {
        return type == GameType.VIRTUAL_ONLINE;
    }

    public boolean isVirtualOffline() {
        return type == GameType.VIRTUAL_OFFLINE;
    }
}
