package ru.prohor.universe.yahtzee.offline.data.inner.pojo;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.offline.data.inner.dto.OfflineTeamScoresDto;

import java.util.List;

public record OfflineTeamScores(
        List<ObjectId> players,
        Opt<List<OfflineScore>> scores,
        int total,
        Opt<Boolean> hasBonus
) implements MongoEntityPojo<OfflineTeamScoresDto> {
    @Override
    public OfflineTeamScoresDto toDto() {
        return new OfflineTeamScoresDto(
                players,
                scores.map(s -> s.stream().map(OfflineScore::toDto).toList()).orElseNull(),
                total,
                hasBonus.orElseNull()
        );
    }

    public static OfflineTeamScores fromDto(OfflineTeamScoresDto team) {
        return new OfflineTeamScores(
                team.getPlayers(),
                Opt.ofNullable(team.getScores()).map(s -> s.stream().map(OfflineScore::fromDto).toList()),
                team.getTotal(),
                Opt.ofNullable(team.getHasBonus())
        );
    }
}
