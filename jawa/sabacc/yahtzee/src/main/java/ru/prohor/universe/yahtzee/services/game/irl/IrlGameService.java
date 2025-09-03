package ru.prohor.universe.yahtzee.services.game.irl;

import org.bson.types.ObjectId;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.Enumeration;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.core.collections.Result;
import ru.prohor.universe.jocasta.jodaTime.DateTimeUtil;
import ru.prohor.universe.yahtzee.Yahtzee;
import ru.prohor.universe.yahtzee.data.MongoRepositoryWithWrapper;
import ru.prohor.universe.yahtzee.data.entities.pojo.IrlGame;
import ru.prohor.universe.yahtzee.data.entities.pojo.IrlRoom;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.data.inner.pojo.IrlInterimTeamScores;
import ru.prohor.universe.yahtzee.data.inner.pojo.IrlScore;
import ru.prohor.universe.yahtzee.data.inner.pojo.IrlTeamScores;
import ru.prohor.universe.yahtzee.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.web.controllers.GameIrlController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class IrlGameService {
    private static final int FIRST_MOVE_INDEX = 0;

    private final int maxTeams;
    private final int maxPlayersInTeam;
    private final MongoRepositoryWithWrapper<?, Player> playerRepository;
    private final MongoRepositoryWithWrapper<?, IrlGame> gameRepository;
    private final MongoRepositoryWithWrapper<?, IrlRoom> roomRepository;
    private final GameColorsService gameColorsService;

    public IrlGameService(
            @Value("${universe.yahtzee.game.irl.max-teams}") int maxTeams,
            @Value("${universe.yahtzee.game.irl.max-players-in-team}") int maxPlayersInTeam,
            MongoRepositoryWithWrapper<?, Player> playerRepository,
            MongoRepositoryWithWrapper<?, IrlGame> gameRepository,
            MongoRepositoryWithWrapper<?, IrlRoom> roomRepository,
            GameColorsService gameColorsService
    ) {
        this.maxTeams = maxTeams;
        this.maxPlayersInTeam = maxPlayersInTeam;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.roomRepository = roomRepository;
        this.gameColorsService = gameColorsService;
    }

    public Opt<GameIrlController.CurrentRoomResponse> getCurrentRoom(Player player) {
        return player.currentRoom().map(
                id -> roomRepository.findById(id).map(
                        room -> new GameIrlController.CurrentRoomResponse(
                                room.id().toHexString(),
                                DateTimeUtil.toReadableString(room.createdAt()),
                                room.teams().size()
                        )
                ).orElseThrow(
                        () -> roomNotFound(id)
                )
        );
    }

    public Opt<GameIrlController.RoomInfoResponse> getRoomInfo(Player player) {
        return player.currentRoom().map(
                id -> roomRepository.findById(id).map(
                        room -> new GameIrlController.RoomInfoResponse(
                                Enumeration.enumerateAndMap(
                                        room.teams(),
                                        (i, team) -> new GameIrlController.TeamInfo(
                                                team.teamId(),
                                                team.title(),
                                                gameColorsService.getById(team.color()),
                                                i == room.movingTeamIndex(),
                                                Enumeration.enumerateAndMap(
                                                        team.players(),
                                                        (j, pl) -> new GameIrlController.PlayerInfo(
                                                                player.id().toHexString(),
                                                                player.displayName(),
                                                                j == team.movingPlayerIndex()
                                                        )
                                                )
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
            GameIrlController.CreateRoomRequest body
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
        for (GameIrlController.TeamPlayers teamPlayers : body.teams()) {
            playersInTeams.put(
                    teamPlayers.title(),
                    teamPlayers.playersIds().stream().map(players::get).toList()
            );
        }
        Map<String, GameIrlController.TeamColor> teamsColors = gameColorsService.calculateColorsForTeams(
                playersInTeams
        );
        List<IrlScore> emptyScores = List.of();
        AtomicInteger currentTeamIndex = new AtomicInteger(0);

        IrlRoom room = new IrlRoom(
                newRoomId,
                Instant.now(),
                player.id(),
                FIRST_MOVE_INDEX,
                playersInTeams.entrySet().stream().map(entry -> new IrlInterimTeamScores(
                        currentTeamIndex.getAndIncrement(),
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

    public Result<GameIrlController.SaveMoveResponse> saveMove(
            Player player,
            GameIrlController.SaveMoveRequest body
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
        Opt<IrlRoom> roomO = roomRepository.findById(player.currentRoom().get());
        if (roomO.isEmpty())
            throw roomNotFound(player.currentRoom().get());
        IrlRoom room = roomO.get();
        IrlInterimTeamScores movingTeam = room.teams().get(room.movingTeamIndex());
        if (!movingTeam.players().contains(mover.id()))
            return Result.error("Moving player does not belong to the current moving team");
        String combination = body.combination().propertyName();
        if (movingTeam.scores().stream().anyMatch(score -> score.combination().equals(combination)))
            return Result.error("Moving team already has combination " + combination);
        if (!movingTeam.players().get(movingTeam.movingPlayerIndex()).equals(mover.id()))
            return Result.error(
                    "Illegal moving player, expect ObjectId " + movingTeam.players().get(movingTeam.movingPlayerIndex())
            );
        List<IrlScore> scores = new ArrayList<>(movingTeam.scores());
        scores.add(new IrlScore(body.combination().propertyName(), body.value()));
        int newMovingPlayerIndex = movingTeam.movingPlayerIndex() + 1;
        if (newMovingPlayerIndex == movingTeam.players().size())
            newMovingPlayerIndex = 0;
        List<IrlInterimTeamScores> teams = new ArrayList<>(room.teams());
        teams.set(
                room.movingTeamIndex(),
                movingTeam.toBuilder().scores(scores).movingPlayerIndex(newMovingPlayerIndex).build()
        );
        int newMovingTeamIndex = room.movingTeamIndex() + 1;
        if (newMovingTeamIndex == room.teams().size())
            newMovingTeamIndex = 0;
        room = room.toBuilder().teams(teams).movingTeamIndex(newMovingTeamIndex).build();
        roomRepository.save(room);
        IrlInterimTeamScores nextMovingTeam = room.teams().get(newMovingTeamIndex);
        if (nextMovingTeam.scores().size() == Yahtzee.COMBINATIONS) {
            gameRepository.save(roomToGame(room));
            return Result.of(new GameIrlController.SaveMoveResponse(null, true));
        }

        return Result.of(new GameIrlController.SaveMoveResponse(
                nextMovingTeam.players().get(nextMovingTeam.movingPlayerIndex()).toHexString(),
                false
        ));
        // TODO понять зачем я делал teamId
    }

    private IrlGame roomToGame(IrlRoom room) {
        return new IrlGame(
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
                                    .mapToInt(IrlScore::value)
                                    .sum() >= Yahtzee.BONUS_CONDITION;
                            return new IrlTeamScores(
                                    team.players(),
                                    Opt.of(team.scores()),
                                    team.scores()
                                            .stream()
                                            .mapToInt(IrlScore::value)
                                            .sum() + (hasBonus ? Yahtzee.BONUS_VALUE : 0),
                                    Opt.of(hasBonus)
                            );
                        })
                        .toList()
        );
    }

    private List<Player> validateAndFindPlayers(
            Player player,
            GameIrlController.CreateRoomRequest body,
            ObjectId newRoomId
    ) throws RoomCreationException {
        if (player.currentRoom().isPresent())
            throw new RoomCreationException("Player already linked to room {" + player.currentRoom().get() + "}");
        if (body.teams().isEmpty() || body.teams().size() > maxTeams)
            throw new RoomCreationException("Room must have from 1 to " + maxTeams + " teams");

        Set<String> used = new HashSet<>();
        for (GameIrlController.TeamPlayers teamPlayers : body.teams()) {
            if (used.contains(teamPlayers.title()))
                throw new RoomCreationException("Team title \"" + teamPlayers.title() + "\" used multiple times");
            used.add(teamPlayers.title());
        }

        used.clear();
        List<ObjectId> playersIds = new ArrayList<>();
        try {
            for (GameIrlController.TeamPlayers teamPlayers : body.teams()) {
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
