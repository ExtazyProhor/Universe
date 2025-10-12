package ru.prohor.universe.yahtzee.services.game.offline;

import org.bson.types.ObjectId;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.Enumeration;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.common.Result;
import ru.prohor.universe.jocasta.jodaTime.DateTimeUtil;
import ru.prohor.universe.yahtzee.core.TeamColor;
import ru.prohor.universe.yahtzee.core.Yahtzee;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.Combination;
import ru.prohor.universe.yahtzee.data.entities.pojo.OfflineGame;
import ru.prohor.universe.yahtzee.data.entities.pojo.OfflineRoom;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.data.inner.pojo.OfflineInterimTeamScores;
import ru.prohor.universe.yahtzee.data.inner.pojo.OfflineScore;
import ru.prohor.universe.yahtzee.data.inner.pojo.OfflineTeamScores;
import ru.prohor.universe.yahtzee.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.web.controllers.OfflineGameController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfflineGameService {
    private static final int FIRST_MOVE_INDEX = 0;

    private final int maxTeams;
    private final int maxPlayersInTeam;
    private final MongoRepository<Player> playerRepository;
    private final MongoRepository<OfflineGame> gameRepository;
    private final MongoRepository<OfflineRoom> roomRepository;
    private final GameColorsService gameColorsService;

    public OfflineGameService(
            @Value("${universe.yahtzee.game.offline.max-teams}") int maxTeams,
            @Value("${universe.yahtzee.game.offline.max-players-in-team}") int maxPlayersInTeam,
            MongoRepository<Player> playerRepository,
            MongoRepository<OfflineGame> gameRepository,
            MongoRepository<OfflineRoom> roomRepository,
            GameColorsService gameColorsService
    ) {
        this.maxTeams = maxTeams;
        this.maxPlayersInTeam = maxPlayersInTeam;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.roomRepository = roomRepository;
        this.gameColorsService = gameColorsService;
    }

    public Opt<OfflineGameController.CurrentRoomResponse> getCurrentRoom(Player player) {
        return player.currentRoom().map(
                id -> roomRepository.findById(id).map(
                        room -> new OfflineGameController.CurrentRoomResponse(
                                DateTimeUtil.toReadableString(room.createdAt()),
                                room.teams().size()
                        )
                ).orElseThrow(
                        () -> roomNotFound(id)
                )
        );
    }

    public Opt<OfflineGameController.RoomInfoResponse> getRoomInfo(Player player) {
        return player.currentRoom().map(
                id -> roomRepository.findById(id).map(
                        room -> new OfflineGameController.RoomInfoResponse(
                                Enumeration.enumerateAndMap(
                                        room.teams(),
                                        (i, team) -> new OfflineGameController.TeamInfo(
                                                team.title(),
                                                gameColorsService.getById(team.color()),
                                                i == room.movingTeamIndex(),
                                                Enumeration.enumerateAndMap(
                                                        team.players(),
                                                        (j, pl) -> new OfflineGameController.PlayerInfo(
                                                                player.id().toHexString(),
                                                                player.displayName(),
                                                                j == team.movingPlayerIndex()
                                                        )
                                                ),
                                                team.scores().stream().map(
                                                        score -> new OfflineGameController.CombinationInfo(
                                                                Combination.of(score.combination()),
                                                                score.value()
                                                        )
                                                ).toList()
                                        )
                                )
                        )
                ).orElseThrow(
                        () -> roomNotFound(id)
                )
        );
    }

    public Opt<String> createRoom(
            Player player,
            OfflineGameController.CreateRoomRequest body
    ) {
        ObjectId newRoomId = ObjectId.get();
        Map<String, Player> players;
        try {
            players = validateAndFindPlayers(player, body, newRoomId).stream().collect(Collectors.toMap(
                    p -> p.id().toHexString(),
                    p -> p
            ));
        } catch (RoomCreationException e) {
            return Opt.of(e.getMessage());
        }

        Map<String, List<Player>> playersInTeams = new HashMap<>();
        for (OfflineGameController.TeamPlayers teamPlayers : body.teams()) {
            playersInTeams.put(
                    teamPlayers.title(),
                    teamPlayers.playersIds().stream().map(players::get).toList()
            );
        }
        Map<String, TeamColor> teamsColors = gameColorsService.calculateColorsForTeams(
                playersInTeams
        );
        List<OfflineScore> emptyScores = List.of();

        OfflineRoom room = new OfflineRoom(
                newRoomId,
                Instant.now(),
                player.id(),
                FIRST_MOVE_INDEX,
                playersInTeams.entrySet().stream().map(entry -> new OfflineInterimTeamScores(
                        FIRST_MOVE_INDEX,
                        entry.getKey(),
                        teamsColors.get(entry.getKey()).colorId(),
                        entry.getValue().stream().map(Player::id).toList(),
                        emptyScores
                )).toList()
        );
        roomRepository.save(room);
        return Opt.empty();
    }

    public Result<OfflineGameController.SaveMoveResponse> saveMove(
            Player player,
            OfflineGameController.SaveMoveRequest body
    ) {
        if (player.currentRoom().isEmpty())
            return Result.error("Player is not linked to room");
        ObjectId moverId;
        try {
            moverId = new ObjectId(body.movingPlayerId());
        } catch (Exception e) {
            return Result.error("Illegal ObjectId format");
        }
        Opt<Player> moverO = playerRepository.findById(moverId);
        if (moverO.isEmpty())
            return Result.error("Moving player does not exist");
        if (!Yahtzee.isValidCombinationValue(body.combination(), body.value()))
            return Result.error("Illegal value " + body.value() + " for combination " + body.combination());
        Player mover = moverO.get();
        if (mover.currentRoom().isEmpty())
            return Result.error("Mover is not linked to room");
        if (!mover.currentRoom().get().equals(player.currentRoom().get()))
            return Result.error("Player and mover are in different rooms");
        Opt<OfflineRoom> roomO = roomRepository.findById(player.currentRoom().get());
        if (roomO.isEmpty())
            throw roomNotFound(player.currentRoom().get());
        OfflineRoom room = roomO.get();
        OfflineInterimTeamScores movingTeam = room.teams().get(room.movingTeamIndex());
        if (!movingTeam.players().contains(mover.id()))
            return Result.error("Moving player does not belong to the current moving team");
        String combination = body.combination().propertyName();
        if (movingTeam.scores().stream().anyMatch(score -> score.combination().equals(combination)))
            return Result.error("Moving team already has combination " + combination);
        if (!movingTeam.players().get(movingTeam.movingPlayerIndex()).equals(mover.id()))
            return Result.error(
                    "Illegal moving player, expect ObjectId " + movingTeam.players().get(movingTeam.movingPlayerIndex())
            );
        List<OfflineScore> scores = new ArrayList<>(movingTeam.scores());
        scores.add(new OfflineScore(body.combination().propertyName(), body.value()));
        int newMovingPlayerIndex = movingTeam.movingPlayerIndex() + 1;
        if (newMovingPlayerIndex == movingTeam.players().size())
            newMovingPlayerIndex = 0;
        List<OfflineInterimTeamScores> teams = new ArrayList<>(room.teams());
        teams.set(
                room.movingTeamIndex(),
                movingTeam.toBuilder().scores(scores).movingPlayerIndex(newMovingPlayerIndex).build()
        );
        int newMovingTeamIndex = room.movingTeamIndex() + 1;
        if (newMovingTeamIndex == room.teams().size())
            newMovingTeamIndex = 0;
        room = room.toBuilder().teams(teams).movingTeamIndex(newMovingTeamIndex).build();
        roomRepository.save(room);
        OfflineInterimTeamScores nextMovingTeam = room.teams().get(newMovingTeamIndex);
        if (nextMovingTeam.scores().size() == Yahtzee.COMBINATIONS) {
            gameRepository.save(roomToGame(room));
            return Result.of(new OfflineGameController.SaveMoveResponse(null, true));
        }

        return Result.of(new OfflineGameController.SaveMoveResponse(
                nextMovingTeam.players().get(nextMovingTeam.movingPlayerIndex()).toHexString(),
                false
        ));
    }

    private OfflineGame roomToGame(OfflineRoom room) {
        return new OfflineGame(
                ObjectId.get(),
                LocalDate.now(DateTimeZone.UTC),
                Opt.of(LocalTime.now(DateTimeZone.UTC)),
                room.initiator(),
                room.teams()
                        .stream()
                        .map(team -> {
                            boolean hasBonus = team.scores()
                                    .stream()
                                    .filter(score -> Yahtzee.isSimple(score.combination()))
                                    .mapToInt(OfflineScore::value)
                                    .sum() >= Yahtzee.BONUS_CONDITION;
                            return new OfflineTeamScores(
                                    team.players(),
                                    Opt.of(team.scores()),
                                    team.scores()
                                            .stream()
                                            .mapToInt(OfflineScore::value)
                                            .sum() + (hasBonus ? Yahtzee.BONUS_VALUE : 0),
                                    Opt.of(hasBonus)
                            );
                        })
                        .toList()
        );
    }

    private List<Player> validateAndFindPlayers(
            Player player,
            OfflineGameController.CreateRoomRequest body,
            ObjectId newRoomId
    ) throws RoomCreationException {
        if (player.currentRoom().isPresent())
            throw new RoomCreationException("Player already linked to room {" + player.currentRoom().get() + "}");
        if (body.teams().isEmpty() || body.teams().size() > maxTeams)
            throw new RoomCreationException("Room must have from 1 to " + maxTeams + " teams");

        Set<String> used = new HashSet<>();
        for (OfflineGameController.TeamPlayers teamPlayers : body.teams()) {
            if (used.contains(teamPlayers.title()))
                throw new RoomCreationException("Team title \"" + teamPlayers.title() + "\" used multiple times");
            used.add(teamPlayers.title());
        }

        used.clear();
        List<ObjectId> playersIds = new ArrayList<>();
        try {
            for (OfflineGameController.TeamPlayers teamPlayers : body.teams()) {
                if (teamPlayers.playersIds().isEmpty() || teamPlayers.playersIds().size() > maxPlayersInTeam)
                    throw new RoomCreationException("Team must have from 1 to " + maxPlayersInTeam + " players");
                for (String id : teamPlayers.playersIds()) {
                    playersIds.add(new ObjectId(id));
                    if (used.contains(id))
                        throw new RoomCreationException("Duplicate playerId: {" + id + "}");
                    used.add(id);
                }
            }
        } catch (Exception e) {
            throw new RoomCreationException("Illegal format of ObjectId {" + e.getMessage() + "}");
        }
        List<Player> players = playerRepository.findAllByIds(playersIds);
        if (players.size() != playersIds.size())
            throw new RoomCreationException(playersIds.size() - players.size() + " players not found in the database");
        if (players.stream().anyMatch(p -> p.currentRoom().isPresent()))
            throw new RoomCreationException("Some players already have linked rooms");
        playerRepository.save(
                players.stream().map(p -> p.toBuilder().currentRoom(Opt.of(newRoomId)).build()).toList()
        );
        return players;
    }

    private static RuntimeException roomNotFound(ObjectId id) {
        return new RuntimeException("Server error: room {" + id + "} not found");
    }
}
