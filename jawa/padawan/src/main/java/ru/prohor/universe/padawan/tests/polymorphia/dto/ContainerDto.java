package ru.prohor.universe.padawan.tests.polymorphia.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import ru.prohor.universe.padawan.tests.polymorphia.FigureType;

@Entity("test_polymorphism")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class ContainerDto {
    @Id
    private ObjectId id;

    private String name;
    private FigureType type;
    private FigureDto figure;
}
