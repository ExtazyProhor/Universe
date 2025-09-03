package ru.prohor.universe.yahtzee.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Entity("users")
public class PlayerDto {
    @Id
    private ObjectId id;
    @Property("uuid")
    private UUID uuid;
    @Property("numeric_id")
    private long numericId;
    private String username;
    private int color;
    @Property("display_name")
    private String displayName;
    private List<ObjectId> friends;
    @Property("current_room")
    private ObjectId currentRoom;
    @Property("image_id")
    private ObjectId imageId;
    @Property("created_at")
    private Instant createdAt;
    private boolean trusted;

    @SuppressWarnings("unused")
    public PlayerDto() {}

    public PlayerDto(
            ObjectId id,
            UUID uuid,
            long numericId,
            String username,
            int color,
            String displayName,
            List<ObjectId> friends,
            ObjectId currentRoom,
            ObjectId imageId,
            Instant createdAt,
            boolean trusted
    ) {
        this.id = id;
        this.uuid = uuid;
        this.numericId = numericId;
        this.username = username;
        this.color = color;
        this.displayName = displayName;
        this.friends = friends;
        this.currentRoom = currentRoom;
        this.imageId = imageId;
        this.createdAt = createdAt;
        this.trusted = trusted;
    }
}
