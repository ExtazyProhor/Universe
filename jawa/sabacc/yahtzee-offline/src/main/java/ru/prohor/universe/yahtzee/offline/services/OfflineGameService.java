package ru.prohor.universe.yahtzee.offline.services;

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
import ru.prohor.universe.yahtzee.core.core.TeamColor;
import ru.prohor.universe.yahtzee.core.core.Yahtzee;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.core.Combination;
import ru.prohor.universe.yahtzee.offline.api.CombinationInfo;
import ru.prohor.universe.yahtzee.offline.api.CreateRoomRequest;
import ru.prohor.universe.yahtzee.offline.api.PlayerInfo;
import ru.prohor.universe.yahtzee.offline.api.RoomInfoResponse;
import ru.prohor.universe.yahtzee.offline.api.SaveMoveRequest;
import ru.prohor.universe.yahtzee.offline.api.SaveMoveResponse;
import ru.prohor.universe.yahtzee.offline.api.TeamInfo;
import ru.prohor.universe.yahtzee.offline.api.TeamPlayers;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineGame;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineRoom;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineInterimTeamScores;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineScore;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineTeamScores;
import ru.prohor.universe.yahtzee.core.data.inner.pojo.RoomReference;
import ru.prohor.universe.yahtzee.core.core.RoomType;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;

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

    public Opt<RoomInfoResponse> getRoomInfo(Player player) {
        return player.currentRoom().filter(ref -> ref.type() == RoomType.TACTILE_OFFLINE).map(RoomReference::id).map(
                id -> roomRepository.findById(id).map(
                        room -> new RoomInfoResponse(
                                Enumeration.enumerateAndMap(
                                        room.teams(),
                                        (i, team) -> teamScoresEnumerationMapper(i, team, room, player)
                                )
                        )
                ).orElseThrow(
                        // TODO mongo ensure
                        () -> {
                            throw new RuntimeException(
                                    "Server error: " +
                                            RoomType.TACTILE_OFFLINE.propertyName() +
                                            " room {" + id + "} not found"
                            );
                        }
                )
        );
    }

    public TeamInfo teamScoresEnumerationMapper(
            int i,
            OfflineInterimTeamScores team,
            OfflineRoom room,
            Player player
    ) {
        return new TeamInfo(
                team.title(),
                gameColorsService.getById(team.color()),
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
                                Combination.of(score.combination()),
                                score.value()
                        )
                ).toList()
        );
    }

    public Opt<String> createRoom(
            Player player,
            CreateRoomRequest body
    ) {
        ObjectId newRoomId = ObjectId.get();
        Map<String, Player> players;
        Result<List<Player>> playersResult = validateAndFindPlayers(player, body, newRoomId);
        if (playersResult.isError())
            return Opt.of(playersResult.error());
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

    public Result<SaveMoveResponse> saveMove(
            Player player,
            SaveMoveRequest body
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
        if (player.currentRoom().get().type() != RoomType.TACTILE_OFFLINE)
            return Result.error("Illegal room type: " + player.currentRoom().get().type().propertyName());
        Opt<OfflineRoom> roomO = roomRepository.findById(player.currentRoom().get().id());
        if (roomO.isEmpty())
            throw new RuntimeException(
                    "Server error: " +
                            RoomType.TACTILE_OFFLINE.propertyName() +
                            " room {" + player.currentRoom().get().id() + "} not found"
            );
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
            return Result.of(new SaveMoveResponse(null, true));
        }

        return Result.of(new SaveMoveResponse(
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
                        .map(this::offlineTeamScoresMapper)
                        .toList()
        );
    }

    private OfflineTeamScores offlineTeamScoresMapper(OfflineInterimTeamScores team) {
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
    }

    private Result<List<Player>> validateAndFindPlayers(
            Player player,
            CreateRoomRequest body,
            ObjectId newRoomId
    ) {
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
        List<Player> players = playerRepository.findAllByIds(playersIds);
        if (players.size() != playersIds.size())
            return Result.error(playersIds.size() - players.size() + " players not found in the database");
        if (players.stream().anyMatch(p -> p.currentRoom().isPresent()))
            return Result.error("Some players already have linked rooms");
        playerRepository.save(
                players.stream().map(
                        p -> p.toBuilder()
                                .currentRoom(Opt.of(new RoomReference(newRoomId, RoomType.TACTILE_OFFLINE)))
                                .build()
                ).toList());
        return Result.of(players);
    }
}
