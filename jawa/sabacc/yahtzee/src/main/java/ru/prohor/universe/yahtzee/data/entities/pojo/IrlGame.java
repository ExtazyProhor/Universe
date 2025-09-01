package ru.prohor.universe.yahtzee.data.entities.pojo;

import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.jodaTime.DateTimeUtil;
import ru.prohor.universe.yahtzee.data.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.entities.dto.IrlGameDto;
import ru.prohor.universe.yahtzee.data.inner.pojo.IrlTeamScores;

import java.util.List;

public record IrlGame(
        ObjectId id,
        LocalDate date,
        Opt<LocalTime> finishTime,
        ObjectId initiator,
        List<IrlTeamScores> teams
) implements MongoEntityPojo<IrlGameDto> {
    @Override
    public IrlGameDto toDto() {
        return new IrlGameDto(
                id,
                DateTimeUtil.unwrap(date),
                finishTime.map(DateTimeUtil::unwrap).orElseNull(),
                initiator,
                teams.stream().map(IrlTeamScores::toDto).toList()
        );
    }

    public static IrlGame fromDto(IrlGameDto game) {
        return new IrlGame(
                game.getId(),
                DateTimeUtil.wrap(game.getDate()),
                Opt.ofNullable(game.getFinishTime()).map(DateTimeUtil::wrap),
                game.getInitiator(),
                game.getTeams().stream().map(IrlTeamScores::fromDto).toList()
        );
    }
}
