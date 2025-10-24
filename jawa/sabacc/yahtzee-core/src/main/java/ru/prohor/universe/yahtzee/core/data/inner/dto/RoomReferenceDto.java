package ru.prohor.universe.yahtzee.core.data.inner.dto;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RoomReferenceDto {
    @Getter
    private ObjectId id;
    @Getter
    private String type;
}
