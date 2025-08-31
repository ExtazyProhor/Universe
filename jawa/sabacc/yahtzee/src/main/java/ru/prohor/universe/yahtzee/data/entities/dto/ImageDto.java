package ru.prohor.universe.yahtzee.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import java.time.Instant;

@Entity("images")
public class ImageDto {
    @Id
    private ObjectId id;
    private Binary content;
    @Property("created_at")
    private Instant createdAt;

    public ImageDto() {}

    public ImageDto(
            ObjectId id,
            Binary content,
            Instant createdAt
    ) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public ObjectId getId() {
        return id;
    }

    public Binary getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
