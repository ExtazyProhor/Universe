package ru.prohor.universe.yahtzee.web.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.core.Combination;
import ru.prohor.universe.yahtzee.core.TeamColor;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.services.game.offline.OfflineGameService;

import java.util.List;

@RestController
@RequestMapping("/api/game/offline")
public class OfflineGameController {
    private final OfflineGameService offlineGameService;

    public OfflineGameController(OfflineGameService offlineGameService) {
        this.offlineGameService = offlineGameService;
    }

    @PostMapping("/current_room")
    public ResponseEntity<CurrentRoomResponse> currentRoom(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return offlineGameService.getCurrentRoom(player.get()).map(
                ResponseEntity::ok
        ).orElseGet(
                () -> ResponseEntity.notFound().build()
        );
    }

    public record CurrentRoomResponse(
            String creation, // datetime
            int teams
    ) {}

    @GetMapping("/room_info")
    public ResponseEntity<RoomInfoResponse> getRoomInfo(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return offlineGameService.getRoomInfo(player.get()).map(
                ResponseEntity::ok
        ).orElseGet(
                () -> ResponseEntity.notFound().build()
        );
    }

    public record RoomInfoResponse(
            List<TeamInfo> teams
    ) {}

    public record TeamInfo(
            String title,
            TeamColor color,
            boolean moving, // next move is up to this team
            List<PlayerInfo> players,
            List<CombinationInfo> results // combinations that already filled
    ) {}

    public record CombinationInfo(
            Combination combination,
            int value
    ) {}

    public record PlayerInfo(
            String id,
            String name,
            boolean moving // next move of his team is up to this player
    ) {}

    @PostMapping("/create_room")
    public ResponseEntity<?> createRoom(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestBody
            CreateRoomRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return offlineGameService.createRoom(player.get(), body).map(
                error -> ResponseEntity.badRequest().body(new CreateRoomResponse(error))
        ).orElseGet(
                () -> ResponseEntity.ok().build()
        );
    }

    public record CreateRoomRequest(
            @NotNull
            @Size(min = 1, max = 8)
            List<TeamPlayers> teams
    ) {}

    public record TeamPlayers(
            @NotNull
            @Size(min = 2, max = 20)
            String title,
            @JsonProperty("players_ids")
            List<String> playersIds
    ) {}

    public record CreateRoomResponse(
            String error // optional error message
    ) {}

    @PostMapping("/save_move")
    public ResponseEntity<?> saveMove(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestBody
            SaveMoveRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return offlineGameService.saveMove(player.get(), body).mapOrElse(
                ResponseEntity::ok,
                error -> ResponseEntity.badRequest().body(new SaveMoveErrorResponse(error))
        );
    }

    public record SaveMoveRequest(
            @JsonProperty("moving_player_id")
            String movingPlayerId,
            Combination combination,
            int value
    ) {}

    public record SaveMoveResponse(
            @JsonProperty("next_move_player_id")
            String nextMovePlayerId,
            @JsonProperty("game_over") // if true - next_move_player_id is absent, show dialog window
            boolean gameOver
    ) {}

    public record SaveMoveErrorResponse(
            String error
    ) {}
}
