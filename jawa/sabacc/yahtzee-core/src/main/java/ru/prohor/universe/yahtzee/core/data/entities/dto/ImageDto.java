package ru.prohor.universe.yahtzee.core.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("images")
public class ImageDto {
    @Id
    private ObjectId id;
    private Binary content;
    @Property("created_at")
    private Instant createdAt;
}
