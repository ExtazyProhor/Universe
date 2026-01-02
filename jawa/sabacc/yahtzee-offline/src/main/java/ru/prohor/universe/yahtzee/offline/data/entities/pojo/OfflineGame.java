package ru.prohor.universe.yahtzee.offline.data.entities.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.offline.data.entities.dto.OfflineGameDto;
import ru.prohor.universe.yahtzee.offline.data.inner.OfflineGameSource;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineTeamScores;

import java.time.Instant;
import java.util.List;

public record OfflineGame(
        ObjectId id,
        Instant date,
        ObjectId initiator,
        List<OfflineTeamScores> teams,
        boolean trusted,
        OfflineGameSource source,
        Opt<ObjectId> room
) implements MongoEntityPojo<OfflineGameDto> {
    @Override
    public OfflineGameDto toDto() {
        return new OfflineGameDto(
                id,
                date,
                initiator,
                teams.stream().map(OfflineTeamScores::toDto).toList(),
                trusted,
                source,
                room.orElseNull()
        );
    }

    public static OfflineGame fromDto(OfflineGameDto game) {
        return new OfflineGame(
                game.getId(),
                game.getDate(),
                game.getInitiator(),
                game.getTeams().stream().map(OfflineTeamScores::fromDto).toList(),
                game.isTrusted(),
                game.getSource(),
                Opt.ofNullable(game.getRoom())
        );
    }
}
