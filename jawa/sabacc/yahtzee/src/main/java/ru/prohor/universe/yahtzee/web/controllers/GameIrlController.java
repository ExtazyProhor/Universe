package ru.prohor.universe.yahtzee.web.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.services.game.irl.IrlGameService;

import java.util.List;

@RestController("/api/game/irl")
public class GameIrlController {
    private final IrlGameService irlGameService;

    public GameIrlController(IrlGameService irlGameService) {
        this.irlGameService = irlGameService;
    }

    @PostMapping("/current_room")
    public ResponseEntity<CurrentRoomResponse> currentRoom(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return irlGameService.getCurrentRoom(player.get()).map(
                ResponseEntity::ok
        ).orElseGet(
                () -> ResponseEntity.notFound().build()
        );
    }

    public record CurrentRoomResponse(
            String id,
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
        return irlGameService.getRoomInfo(player.get()).map(
                ResponseEntity::ok
        ).orElseGet(
                () -> ResponseEntity.notFound().build()
        );
    }

    public record RoomInfoResponse(
            List<TeamInfo> teams
    ) {}

    public record TeamInfo(
            @JsonProperty("team_id")
            int teamId,
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
        return irlGameService.createRoom(player.get(), body).map(
                error -> ResponseEntity.badRequest().body(new CreateRoomResponse(error))
        ).orElseGet(
                () -> ResponseEntity.ok().build()
        );
    }

    public record CreateRoomRequest(
            List<TeamPlayers> teams
    ) {}

    public record TeamPlayers(
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
        return irlGameService.saveMove(player.get(), body).mapOrElse(
                ResponseEntity::ok,
                error -> ResponseEntity.badRequest().body(new SaveMoveErrorResponse(error))
        );
    }

    public record SaveMoveRequest(
            @JsonProperty("team_id")
            int teamId,
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

    public record TeamColor(
            @JsonIgnore
            int colorId, // internal use
            @JsonProperty("bg")
            String background,
            String text,
            String light,
            String dark
    ) {}

    public enum Combination {
        @JsonProperty("units")
        UNITS,
        @JsonProperty("twos")
        TWOS,
        @JsonProperty("threes")
        THREES,
        @JsonProperty("fours")
        FOURS,
        @JsonProperty("fives")
        FIVES,
        @JsonProperty("sixes")
        SIXES,

        @JsonProperty("pair")
        PAIR,
        @JsonProperty("two_pairs")
        TWO_PAIRS,
        @JsonProperty("three_of_kind")
        THREE_OF_KIND,
        @JsonProperty("four_of_kind")
        FOUR_OF_KIND,
        @JsonProperty("full_house")
        FULL_HOUSE,
        @JsonProperty("low_straight")
        LOW_STRAIGHT,
        @JsonProperty("high_straight")
        HIGH_STRAIGHT,
        @JsonProperty("yahtzee")
        YAHTZEE,
        @JsonProperty("chance")
        CHANCE;

        public String propertyName() {
            return name().toLowerCase();
        }

        public static Combination of(String propertyName) {
            return Enum.valueOf(Combination.class, propertyName.toUpperCase());
        }
    }
}
