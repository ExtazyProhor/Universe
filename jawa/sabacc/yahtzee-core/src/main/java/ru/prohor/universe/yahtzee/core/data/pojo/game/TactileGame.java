package ru.prohor.universe.yahtzee.core.data.pojo.game;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.utils.CastUtils;
import ru.prohor.universe.yahtzee.core.data.GameType;
import ru.prohor.universe.yahtzee.core.data.dto.game.GameDto;
import ru.prohor.universe.yahtzee.core.data.dto.game.TactileGamePropertiesDto;

import java.time.Instant;
import java.util.List;

public record TactileGame(
        ObjectId id,
        List<ObjectId> players,
        Instant date,
        ObjectId initiator,
        List<Team<TactileScore>> teams,
        boolean trusted,
        Opt<ObjectId> room,
        GameType type,
        TactileGamePropertiesDto properties
) implements Game {
    @Override
    public List<Team<Score>> getTeams() {
        return CastUtils.cast(teams);
    }

    @Override
    public GameDto toDto() {
        return new GameDto(
                id,
                players,
                date,
                initiator,
                teams.stream().map(Team::toDto).toList(),
                trusted,
                room.orElseNull(),
                type,
                properties
        );
    }

    public static TactileGame fromDto(GameDto dto) {
        return new TactileGame(
                dto.getId(),
                dto.getPlayers(),
                dto.getDate(),
                dto.getInitiator(),
                dto.getTeams().stream().map(teamDto -> Team.fromDto(teamDto, TactileScore::fromDto)).toList(),
                dto.isTrusted(),
                Opt.ofNullable(dto.getRoom()),
                dto.getType(),
                CastUtils.cast(dto.getProperties())
        );
    }
}
