package ru.prohor.universe.yahtzee.data.entities.pojo;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.jodaTime.DateTimeUtil;
import ru.prohor.universe.yahtzee.data.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.entities.dto.PlayerDto;

import java.util.List;
import java.util.UUID;

public record Player(
        ObjectId id,
        UUID uuid,
        long numericId,
        String username,
        String color,
        String displayName,
        List<ObjectId> friends,
        Opt<ObjectId> currentRoom,
        Instant createdAt,
        boolean trusted
) implements MongoEntityPojo<PlayerDto> {
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
                currentRoom.orElseNull(),
                DateTimeUtil.unwrap(createdAt),
                trusted
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
                Opt.ofNullable(player.getCurrentRoom()),
                DateTimeUtil.wrap(player.getCreatedAt()),
                player.isTrusted()
        );
    }
}
