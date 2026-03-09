package ru.prohor.universe.padawan.tests.polymorphia.dto;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class SquareDto extends FigureDto {
    private int side;

    public int perimeter() {
        return side * 4;
    }

    public int square() {
        return side * side;
    }
}
