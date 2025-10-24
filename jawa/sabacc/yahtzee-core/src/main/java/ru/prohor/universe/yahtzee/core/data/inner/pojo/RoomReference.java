package ru.prohor.universe.yahtzee.core.data.inner.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.core.RoomType;
import ru.prohor.universe.yahtzee.core.data.inner.dto.RoomReferenceDto;

public record RoomReference(
        ObjectId id,
        RoomType type
) implements MongoEntityPojo<RoomReferenceDto> {
    @Override
    public RoomReferenceDto toDto() {
        return new RoomReferenceDto(id, type.propertyName());
    }

    public static RoomReference fromDto(RoomReferenceDto roomRef) {
        return new RoomReference(roomRef.getId(), RoomType.of(roomRef.getType()));
    }
}
