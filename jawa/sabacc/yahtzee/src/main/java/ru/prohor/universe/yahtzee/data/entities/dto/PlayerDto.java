package ru.prohor.universe.yahtzee.data.entities.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.data.inner.dto.RoomReferenceDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
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
    private RoomReferenceDto currentRoom;
    @Property("image_id")
    private ObjectId imageId;
    @Property("created_at")
    private Instant createdAt;
    private boolean trusted;
}
