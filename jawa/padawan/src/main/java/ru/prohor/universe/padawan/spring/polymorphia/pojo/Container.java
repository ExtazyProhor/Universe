package ru.prohor.universe.padawan.spring.polymorphia.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.padawan.spring.polymorphia.FigureType;
import ru.prohor.universe.padawan.spring.polymorphia.dto.ContainerDto;
import ru.prohor.universe.padawan.spring.polymorphia.dto.FigureDto;
import ru.prohor.universe.padawan.spring.polymorphia.dto.RectangleDto;
import ru.prohor.universe.padawan.spring.polymorphia.dto.SquareDto;

public interface Container extends MongoEntityPojo<ContainerDto> {
    ObjectId id();

    FigureType type();

    static Container fromDto(ContainerDto dto) {
        FigureDto figureDto = dto.getFigure();
        if (figureDto instanceof RectangleDto rectangle) {
            return new RectangleContainer(dto.getId(), dto.getName(), Rectangle.fromDto(rectangle));
        }
        if (figureDto instanceof SquareDto square) {
            return new SquareContainer(dto.getId(), dto.getName(), Square.fromDto(square));
        }
        throw new IllegalArgumentException("Illegal figure type: " + figureDto.getClass());
    }
}
