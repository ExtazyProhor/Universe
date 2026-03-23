package ru.prohor.universe.padawan.tests.polymorphia.pojo;

import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.padawan.tests.polymorphia.dto.RectangleDto;

public record Rectangle(
        int side1,
        int side2
) implements MongoEntityPojo<RectangleDto> {
    @Override
    public RectangleDto toDto() {
        return new RectangleDto(side1, side2);
    }

    public static Rectangle fromDto(RectangleDto dto) {
        return new Rectangle(dto.getSide1(), dto.getSide2());
    }
}
