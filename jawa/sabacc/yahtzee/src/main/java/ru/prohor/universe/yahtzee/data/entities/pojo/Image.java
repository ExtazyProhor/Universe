package ru.prohor.universe.yahtzee.data.entities.pojo;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.joda.time.Instant;
import ru.prohor.universe.jocasta.jodaTime.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.entities.dto.ImageDto;

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
                DateTimeUtil.unwrap(createdAt)
        );
    }

    public static Image fromDto(ImageDto image) {
        return new Image(
                image.getId(),
                image.getContent(),
                DateTimeUtil.wrap(image.getCreatedAt())
        );
    }
}
