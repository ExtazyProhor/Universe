package ru.prohor.universe.yahtzee.data.entities.pojo;

import lombok.Builder;
import org.bson.types.ObjectId;
import org.joda.time.Instant;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.entities.dto.PlayerDto;
import ru.prohor.universe.yahtzee.data.inner.pojo.RoomReference;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record Player(
        ObjectId id,
        UUID uuid,
        long numericId,
        String username,
        int color,
        String displayName,
        List<ObjectId> friends,
        Opt<RoomReference> currentRoom,
        ObjectId imageId,
        Instant createdAt,
        boolean trusted,
        List<ObjectId> outcomingRequests,
        List<ObjectId> incomingRequests
) implements MongoEntityPojo<PlayerDto> {
    public static final String ATTRIBUTE_KEY = "universe.yahtzee-player";

    @Override
    public PlayerDto toDto() {
        return new PlayerDto(
                id,
                uuid,
                numericId,
                username,
                color,
                displayName,
                friends,
                currentRoom.map(RoomReference::toDto).orElseNull(),
                imageId,
                DateTimeUtil.unwrap(createdAt),
                trusted,
                outcomingRequests,
                incomingRequests
        );
    }

    public static Player fromDto(PlayerDto player) {
        return new Player(
                player.getId(),
                player.getUuid(),
                player.getNumericId(),
                player.getUsername(),
                player.getColor(),
                player.getDisplayName(),
                player.getFriends(),
                Opt.ofNullable(player.getCurrentRoom()).map(RoomReference::fromDto),
                player.getImageId(),
                DateTimeUtil.wrap(player.getCreatedAt()),
                player.isTrusted(),
                player.getOutcomingRequests(),
                player.getIncomingRequests()
        );
    }
}
