package ru.prohor.universe.yahtzee.offline.data.entities.pojo;

import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.offline.data.entities.dto.OfflineGameDto;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineTeamScores;

import java.util.List;

public record OfflineGame(
        ObjectId id,
        LocalDate date,
        Opt<LocalTime> finishTime,
        ObjectId initiator,
        List<OfflineTeamScores> teams
) implements MongoEntityPojo<OfflineGameDto> {
    @Override
    public OfflineGameDto toDto() {
        return new OfflineGameDto(
                id,
                DateTimeUtil.unwrap(date),
                finishTime.map(DateTimeUtil::unwrap).orElseNull(),
                initiator,
                teams.stream().map(OfflineTeamScores::toDto).toList()
        );
    }

    public static OfflineGame fromDto(OfflineGameDto game) {
        return new OfflineGame(
                game.getId(),
                DateTimeUtil.wrap(game.getDate()),
                Opt.ofNullable(game.getFinishTime()).map(DateTimeUtil::wrap),
                game.getInitiator(),
                game.getTeams().stream().map(OfflineTeamScores::fromDto).toList()
        );
    }
}
