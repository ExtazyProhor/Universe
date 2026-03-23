package ru.prohor.universe.yahtzee.app.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.Enumeration;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.common.Result;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTransaction;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.yahtzee.app.web.api.CombinationInfo;
import ru.prohor.universe.yahtzee.app.web.api.CreateRoomErrorResponse;
import ru.prohor.universe.yahtzee.app.web.api.CreateRoomRequest;
import ru.prohor.universe.yahtzee.app.web.api.PlayerInfo;
import ru.prohor.universe.yahtzee.app.web.api.RoomInfoResponse;
import ru.prohor.universe.yahtzee.app.web.api.SaveMoveErrorResponse;
import ru.prohor.universe.yahtzee.app.web.api.SaveMoveRequest;
import ru.prohor.universe.yahtzee.app.web.api.SaveMoveResponse;
import ru.prohor.universe.yahtzee.app.web.api.TeamInfo;
import ru.prohor.universe.yahtzee.app.web.api.TeamPlayers;
import ru.prohor.universe.yahtzee.core.Yahtzee;
import ru.prohor.universe.yahtzee.core.color.TeamColor;
import ru.prohor.universe.yahtzee.core.data.Combination;
import ru.prohor.universe.yahtzee.core.data.GameType;
import ru.prohor.universe.yahtzee.core.data.TactileGameSource;
import ru.prohor.universe.yahtzee.core.data.dto.game.TactileGamePropertiesDto;
import ru.prohor.universe.yahtzee.core.data.dto.player.RoomReferenceDto;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Game;
import ru.prohor.universe.yahtzee.core.data.pojo.game.TactileGame;
import ru.prohor.universe.yahtzee.core.data.pojo.game.TactileScore;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Team;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;
import ru.prohor.universe.yahtzee.core.data.pojo.room.TactileIntermediateTeam;
import ru.prohor.universe.yahtzee.core.data.pojo.room.TactileRoom;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TactileGameService {
    private static final int FIRST_MOVE_INDEX = 0;

    private final int maxTeams;
    private final int maxPlayersInTeam;
    private final MongoTransactionService transactionService;
    private final MongoRepository<Player> playerRepository;
    private final MongoRepository<Game> gameRepository;
    private final MongoRepository<TactileRoom> roomRepository;
    private final GameColorsService gameColorsService;

    public TactileGameService(
            @Value("${universe.yahtzee.game.max-teams}") int maxTeams,
            @Value("${universe.yahtzee.game.max-players-in-team}") int maxPlayersInTeam,
            MongoTransactionService transactionService,
            MongoRepository<Player> playerRepository,
            MongoRepository<Game> gameRepository,
            MongoRepository<TactileRoom> roomRepository,
            GameColorsService gameColorsService
    ) {
        this.maxTeams = maxTeams;
        this.maxPlayersInTeam = maxPlayersInTeam;
        this.transactionService = transactionService;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.roomRepository = roomRepository;
        this.gameColorsService = gameColorsService;
    }

    public Opt<RoomInfoResponse> getRoomInfo(Player player) {
        return player.currentRoom()
                .filter(room -> room.getType().isTactile())
                .map(RoomReferenceDto::getId)
                .map(roomRepository::ensuredFindById).map(room -> mapRoomInfoResponse(room, player));
    }

    private RoomInfoResponse mapRoomInfoResponse(TactileRoom room, Player player) {
        return new RoomInfoResponse(
                Enumeration.enumerateAndMap(
                        room.teams(),
                        (i, team) -> teamScoresEnumerationMapper(i, team, room, player)
                )
        );
    }

    public TeamInfo teamScoresEnumerationMapper(
            int i,
            TactileIntermediateTeam team,
            TactileRoom room,
            Player player
    ) {
        Opt<TeamColor> color = gameColorsService.getById(team.color());
        return new TeamInfo(
                team.title(),
                color.isEmpty(),
                color.orElseNull(),
                i == room.movingTeamIndex(),
                Enumeration.enumerateAndMap(
                        team.players(),
                        (j, pl) -> new PlayerInfo(
                                player.id().toHexString(),
                                player.displayName(),
                                j == team.movingPlayerIndex()
                        )
                ),
                team.scores().stream().map(
                        score -> new CombinationInfo(
                                score.combination(),
                                score.value()
                        )
                ).toList()
        );
    }

    public ResponseEntity<?> createRoom(
            Player player,
            CreateRoomRequest body
    ) {
        return transactionService.withTransaction(transaction -> {
            MongoRepository<TactileRoom> transactional = transaction.wrap(roomRepository);
            ObjectId newRoomId = ObjectId.get();
            Instant now = Instant.now();
            Map<String, Player> players;
            Result<List<Player>> playersResult = validateAndFindPlayers(player, body, newRoomId, transaction, now);
            if (playersResult.isError())
                return ResponseEntity.badRequest().body(new CreateRoomErrorResponse(playersResult.error()));
            players = playersResult.result().stream().collect(Collectors.toMap(
                    p -> p.id().toHexString(),
                    p -> p
            ));

            Map<String, List<Player>> playersInTeams = new HashMap<>();
            for (TeamPlayers teamPlayers : body.teams()) {
                playersInTeams.put(
                        teamPlayers.title(),
                        teamPlayers.playersIds().stream().map(players::get).toList()
                );
            }
            Map<String, TeamColor> teamsColors = gameColorsService.calculateColorsForTeams(
                    playersInTeams
            );
            TactileRoom room = new TactileRoom(
                    newRoomId,
                    now,
                    player.id(),
                    FIRST_MOVE_INDEX,
                    playersInTeams.entrySet().stream().map(entry -> new TactileIntermediateTeam(
                            entry.getValue().stream().map(Player::id).toList(),
                            List.of(),
                            FIRST_MOVE_INDEX,
                            entry.getKey(),
                            teamsColors.get(entry.getKey()).colorId()
                    )).toList()
            );
            transactional.save(room);
            return ResponseEntity.ok().build();
        }).asOpt().orElse(ResponseEntity.internalServerError().build());
    }

    public ResponseEntity<?> saveMove(Player player, SaveMoveRequest body) {
        return transactionService.withTransaction(transaction -> {
            ObjectId moverId;
            try {
                moverId = new ObjectId(body.movingPlayerId());
            } catch (Exception e) {
                return saveMoveError("Illegal ObjectId format"); // TODO log [SB]
            }
            MongoRepository<Player> transactionalPlayerRepository = transaction.wrap(playerRepository);
            MongoRepository<TactileRoom> transactionalRoomRepository = transaction.wrap(roomRepository);
            MongoRepository<Game> transactionalGameRepository = transaction.wrap(gameRepository);
            Map<ObjectId, Player> playerAndMover = transactionalPlayerRepository.findAllByIdsAsMap(
                    List.of(player.id(), moverId),
                    Player::id
            );
            if (!playerAndMover.containsKey(moverId))
                return saveMoveError("Moving player does not exist"); // TODO log [SB]
            Player updated = playerAndMover.get(player.id());
            if (updated.currentRoom().isEmpty())
                return saveMoveError("Player is not linked to room");

            if (!Yahtzee.isValidCombinationValue(body.combination(), body.value()))
                return saveMoveError("Illegal value " + body.value() + " for combination " + body.combination()); // TODO log [SB]
            Player mover = playerAndMover.get(moverId);
            if (updated.currentRoom().get().getType().isVirtual())
                return saveMoveError("Illegal room type: " + updated.currentRoom().get().getType()); // TODO log [SB]
            TactileRoom room = transactionalRoomRepository.ensuredFindById(updated.currentRoom().get().getId());
            TactileIntermediateTeam movingTeam = room.teams().get(room.movingTeamIndex());
            if (!movingTeam.players().contains(mover.id()))
                return saveMoveError("Moving player does not belong to the current moving team");
            Combination combination = body.combination();
            if (movingTeam.scores().stream().anyMatch(score -> score.combination() == combination))
                return saveMoveError("Moving team already has combination " + combination);
            if (!movingTeam.players().get(movingTeam.movingPlayerIndex()).equals(mover.id()))
                return saveMoveError(
                        "Illegal moving player, expect ObjectId " + movingTeam.players()
                                .get(movingTeam.movingPlayerIndex())
                );
            List<TactileScore> scores = new ArrayList<>(movingTeam.scores());
            scores.add(new TactileScore(
                    body.combination(),
                    body.value(),
                    Opt.of(moverId),
                    Opt.of(scores.size())
            ));
            int newMovingPlayerIndex = movingTeam.movingPlayerIndex() + 1;
            if (newMovingPlayerIndex == movingTeam.players().size())
                newMovingPlayerIndex = 0;
            List<TactileIntermediateTeam> teams = new ArrayList<>(room.teams());
            teams.set(
                    room.movingTeamIndex(),
                    movingTeam.toBuilder().scores(scores).movingPlayerIndex(newMovingPlayerIndex).build()
            );
            int newMovingTeamIndex = room.movingTeamIndex() + 1;
            if (newMovingTeamIndex == room.teams().size())
                newMovingTeamIndex = 0;
            room = room.toBuilder().teams(teams).movingTeamIndex(newMovingTeamIndex).build();
            transactionalRoomRepository.save(room);
            TactileIntermediateTeam nextMovingTeam = room.teams().get(newMovingTeamIndex);
            if (nextMovingTeam.scores().size() == Yahtzee.COMBINATIONS) {
                Player initiator = transactionalPlayerRepository.ensuredFindById(room.initiator());
                transactionalGameRepository.save(roomToGame(room, initiator));
                return ResponseEntity.ok(new SaveMoveResponse(null, true));
            }

            return ResponseEntity.ok(new SaveMoveResponse(
                    nextMovingTeam.players().get(nextMovingTeam.movingPlayerIndex()).toHexString(),
                    false
            ));
        }).asOpt().orElse(ResponseEntity.internalServerError().build());
    }

    private ResponseEntity<SaveMoveErrorResponse> saveMoveError(String error) {
        return ResponseEntity.badRequest().body(new SaveMoveErrorResponse(error));
    }

    private Game roomToGame(TactileRoom room, Player initiator) {
        return new TactileGame(
                ObjectId.get(),
                room.teams().stream().flatMap(team -> team.players().stream()).toList(),
                Instant.now(),
                initiator.id(),
                room.teams()
                        .stream()
                        .map(this::teamMapper)
                        .toList(),
                initiator.trusted(),
                Opt.of(room.id()),
                GameType.TACTILE_OFFLINE,
                new TactileGamePropertiesDto(TactileGameSource.DIRECT)
        );
    }

    private Team<TactileScore> teamMapper(TactileIntermediateTeam team) {
        boolean hasBonus = team.scores()
                .stream()
                .filter(score -> Yahtzee.isSimple(score.combination()))
                .mapToInt(TactileScore::value)
                .sum() >= Yahtzee.BONUS_CONDITION;
        return new Team<>(
                team.players(),
                Opt.of(team.scores()),
                team.scores()
                        .stream()
                        .mapToInt(TactileScore::value)
                        .sum() + (hasBonus ? Yahtzee.BONUS_VALUE : 0),
                Opt.of(hasBonus),
                Opt.of(team.title()),
                Opt.of(team.color())
        );
    }

    private Result<List<Player>> validateAndFindPlayers(
            Player player,
            CreateRoomRequest body,
            ObjectId newRoomId,
            MongoTransaction transaction,
            Instant now
    ) {
        MongoRepository<Player> transactional = transaction.wrap(playerRepository);
        if (player.currentRoom().isPresent())
            return Result.error("Player already linked to room {" + player.currentRoom().get() + "}");
        if (body.teams().isEmpty() || body.teams().size() > maxTeams)
            return Result.error("Room must have from 1 to " + maxTeams + " teams");

        Set<String> used = new HashSet<>();
        for (TeamPlayers teamPlayers : body.teams()) {
            if (used.contains(teamPlayers.title()))
                return Result.error("Team title \"" + teamPlayers.title() + "\" used multiple times");
            used.add(teamPlayers.title());
        }

        used.clear();
        List<ObjectId> playersIds = new ArrayList<>();
        try {
            for (TeamPlayers teamPlayers : body.teams()) {
                if (teamPlayers.playersIds().isEmpty() || teamPlayers.playersIds().size() > maxPlayersInTeam)
                    return Result.error("Team must have from 1 to " + maxPlayersInTeam + " players");
                for (String id : teamPlayers.playersIds()) {
                    playersIds.add(new ObjectId(id));
                    if (used.contains(id))
                        return Result.error("Duplicate playerId: {" + id + "}");
                    used.add(id);
                }
            }
        } catch (Exception e) {
            return Result.error("Illegal format of ObjectId {" + e.getMessage() + "}");
        }
        List<Player> players = transactional.findAllByIds(playersIds);
        if (players.size() != playersIds.size())
            return Result.error(playersIds.size() - players.size() + " players not found in the database");
        if (players.stream().anyMatch(p -> p.currentRoom().isPresent()))
            return Result.error("Some players already have linked rooms");
        transactional.save(
                player.toBuilder()
                        .currentRoom(Opt.of(new RoomReferenceDto(
                                newRoomId,
                                GameType.TACTILE_OFFLINE,
                                now,
                                body.teams().size(),
                                body.teams().stream().mapToInt(team -> team.playersIds().size()).sum()
                        )))
                        .build()
        );
        return Result.of(players);
    }
}
