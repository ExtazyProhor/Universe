package ru.prohor.universe.padawan.tests.polymorphia.pojo;

import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.padawan.tests.polymorphia.dto.SquareDto;

public record Square(
        int side
) implements MongoEntityPojo<SquareDto> {
    @Override
    public SquareDto toDto() {
        return new SquareDto(side);
    }

    public static Square fromDto(SquareDto dto) {
        return new Square(dto.getSide());
    }
}
