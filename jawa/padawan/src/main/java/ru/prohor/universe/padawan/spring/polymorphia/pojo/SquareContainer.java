package ru.prohor.universe.padawan.spring.polymorphia.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.padawan.spring.polymorphia.FigureType;
import ru.prohor.universe.padawan.spring.polymorphia.dto.ContainerDto;

public record SquareContainer(
        ObjectId id,
        String name,
        Square square
) implements Container {
    @Override
    public FigureType type() {
        return FigureType.SQUARE;
    }

    public ContainerDto toDto() {
        return new ContainerDto(id, name, FigureType.SQUARE, square.toDto());
    }
}
