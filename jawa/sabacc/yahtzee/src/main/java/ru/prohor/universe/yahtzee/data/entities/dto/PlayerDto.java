package ru.prohor.universe.yahtzee.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity("users")
public class PlayerDto {
    @Id
    private ObjectId id;
    @Property("uuid")
    private UUID uuid;
    @Property("numeric_id")
    private long numericId;
    private String username;
    private String color;
    @Property("display_name")
    private String displayName;
    private List<ObjectId> friends;
    @Property("current_room")
    private ObjectId currentRoom;
    @Property("created_at")
    private Instant createdAt;
    private boolean trusted;

    public PlayerDto() {}

    public PlayerDto(
            ObjectId id,
            UUID uuid,
            long numericId,
            String username,
            String color,
            String displayName,
            List<ObjectId> friends,
            ObjectId currentRoom,
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
        this.createdAt = createdAt;
        this.trusted = trusted;
    }

    public ObjectId getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getNumericId() {
        return numericId;
    }

    public String getUsername() {
        return username;
    }

    public String getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<ObjectId> getFriends() {
        return friends;
    }

    public ObjectId getCurrentRoom() {
        return currentRoom;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isTrusted() {
        return trusted;
    }
}
