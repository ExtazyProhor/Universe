package ru.prohor.universe.yahtzee.app.services;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.app.web.api.ColorInfo;
import ru.prohor.universe.yahtzee.app.web.controllers.OfflineGameInfoController;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineGame;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineScore;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineTeamScores;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OfflineGameInfoService {
    private final MongoRepository<OfflineGame> gameRepository;
    private final MongoRepository<Player> playerRepository;
    private final GameColorsService gameColorsService;

    public OfflineGameInfoService(
            MongoRepository<OfflineGame> gameRepository,
            MongoRepository<Player> playerRepository,
            GameColorsService gameColorsService
    ) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gameColorsService = gameColorsService;
    }

    public ResponseEntity<OfflineGameInfoController.OfflineGameInfoResponse> getOfflineGameInfo(String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log SB
            return ResponseEntity.badRequest().build();
        }
        return gameRepository.findById(objectId)
                .map(this::mapGame)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private OfflineGameInfoController.OfflineGameInfoResponse mapGame(OfflineGame game) {
        List<ObjectId> ids = game.teams().stream().flatMap(team -> team.players().stream()).toList();
        Map<ObjectId, OfflineGameInfoController.PlayerInfoResponse> players = playerRepository.ensuredFindAllByIds(ids)
                .stream()
                .map(this::mapPlayer)
                .collect(Collectors.toMap(
                        OfflineGameInfoController.PlayerInfoResponse::id,
                        MonoFunction.identity()
                ));

        return new OfflineGameInfoController.OfflineGameInfoResponse(
                game.id(),
                DateTimeUtil.toReadableString(game.date()),
                players.get(game.initiator()),
                game.teams().stream().map(team -> mapTeam(players, team)).toList(),
                game.trusted()
        );
    }

    private OfflineGameInfoController.TeamInfoResponse mapTeam(
            Map<ObjectId, OfflineGameInfoController.PlayerInfoResponse> players,
            OfflineTeamScores team
    ) {
        return new OfflineGameInfoController.TeamInfoResponse(
                team.players().stream().map(players::get).toList(),
                mapScores(team.scores()),
                team.total(),
                team.hasBonus().orElseNull()
        );
    }

    private List<OfflineGameInfoController.ScoreInfoResponse> mapScores(Opt<List<OfflineScore>> scores) {
        return scores.map(sc -> sc.stream().map(this::mapScore).toList()).orElseNull();
    }

    private OfflineGameInfoController.ScoreInfoResponse mapScore(OfflineScore score) {
        return new OfflineGameInfoController.ScoreInfoResponse(
                score.combination(),
                score.value()
        );
    }

    private OfflineGameInfoController.PlayerInfoResponse mapPlayer(Player player) {
        return new OfflineGameInfoController.PlayerInfoResponse(
                player.id(),
                player.username(),
                ColorInfo.of(gameColorsService.getById(player.color())),
                player.displayName(),
                player.imageId()
        );
    }
}
