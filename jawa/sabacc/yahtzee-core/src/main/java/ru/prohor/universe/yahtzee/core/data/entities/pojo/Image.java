package ru.prohor.universe.yahtzee.core.data.entities.pojo;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.core.data.entities.dto.ImageDto;

import java.time.Instant;

public record Image(
        ObjectId id,
        Binary content,
        Instant createdAt
) implements MongoEntityPojo<ImageDto> {
    @Override
    public ImageDto toDto() {
        return new ImageDto(
                id,
                content,
                createdAt
        );
    }

    public static Image fromDto(ImageDto image) {
        return new Image(
                image.getId(),
                image.getContent(),
                image.getCreatedAt()
        );
    }
}
