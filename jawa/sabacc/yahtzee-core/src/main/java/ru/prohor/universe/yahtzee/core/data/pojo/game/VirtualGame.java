package ru.prohor.universe.yahtzee.core.data.pojo.game;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.utils.CastUtils;
import ru.prohor.universe.yahtzee.core.data.GameType;
import ru.prohor.universe.yahtzee.core.data.dto.game.GameDto;
import ru.prohor.universe.yahtzee.core.data.dto.game.VirtualGamePropertiesDto;

import java.time.Instant;
import java.util.List;

public record VirtualGame(
        ObjectId id,
        List<ObjectId> players,
        Instant date,
        ObjectId initiator,
        List<Team<VirtualScore>> teams,
        boolean trusted,
        ObjectId room,
        GameType type,
        VirtualGamePropertiesDto properties
) implements Game {
    @Override
    public GameDto toDto() {
        return new GameDto(
                id,
                players,
                date,
                initiator,
                teams.stream().map(Team::toDto).toList(),
                trusted,
                room,
                type,
                properties
        );
    }

    public static VirtualGame fromDto(GameDto dto) {
        return new VirtualGame(
                dto.getId(),
                dto.getPlayers(),
                dto.getDate(),
                dto.getInitiator(),
                dto.getTeams().stream().map(teamDto -> Team.fromDto(teamDto, VirtualScore::fromDto)).toList(),
                dto.isTrusted(),
                dto.getRoom(),
                dto.getType(),
                CastUtils.cast(dto.getProperties())
        );
    }
}
