package ru.prohor.universe.padawan.spring.polymorphia.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.padawan.spring.polymorphia.FigureType;
import ru.prohor.universe.padawan.spring.polymorphia.dto.ContainerDto;

public record RectangleContainer(
        ObjectId id,
        String name,
        Rectangle rectangle
) implements Container {
    @Override
    public FigureType type() {
        return FigureType.RECTANGLE;
    }

    public ContainerDto toDto() {
        return new ContainerDto(id, name, FigureType.RECTANGLE, rectangle.toDto());
    }
}
