package ru.prohor.universe.padawan.spring.polymorphia.dto;

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
public class RectangleDto extends FigureDto {
    private int side1;
    private int side2;

    public int perimeter() {
        return side1 * 2 + side2 * 2;
    }

    public int square() {
        return side1 * side2;
    }
}
