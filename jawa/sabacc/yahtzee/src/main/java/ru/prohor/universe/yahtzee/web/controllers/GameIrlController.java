package ru.prohor.universe.yahtzee.web.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.yahtzee.web.YahtzeeAuthorizedUser;

import java.util.List;

@RestController("/api/game/irl")
public class GameIrlController {

    @PostMapping("/current_room")
    public ResponseEntity<CurrentRoomResponse> currentRoom(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user
    ) {
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record CurrentRoomResponse(
            String id,
            String creation, // datetime
            int teams
    ) {}

    @PostMapping("/room_info")
    public ResponseEntity<RoomInfoResponse> getRoomInfo(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            RoomInfoRequest body
    ) {
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record RoomInfoRequest(
            @JsonProperty("room_id")
            String roomId
    ) {}

    public record RoomInfoResponse(
            List<TeamInfo> teams
    ) {}

    public record TeamInfo(
            @JsonProperty("team_id")
            int teamId,
            String color,
            boolean moving, // next move is up to this team
            List<PlayerInfo> players
    ) {}

    public record PlayerInfo(
            String id,
            String name,
            boolean moving // next move of his team is up to this player
    ) {}

    @PostMapping("/create_room")
    public ResponseEntity<?> createRoom(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            CreateRoomRequest body
    ) {
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record CreateRoomRequest(
            List<TeamPlayers> teams
    ) {}

    public record TeamPlayers(
            @JsonProperty("players_ids")
            List<String> playersIds
    ) {}

    @PostMapping("/save_move")
    public ResponseEntity<SaveMoveResponse> saveMove(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            SaveMoveRequest body
    ) {
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record SaveMoveRequest(
            List<Team> teams
    ) {}

    public record SaveMoveResponse(
            @JsonProperty("next_move_player_id")
            String nextMovePlayerId
    ) {}

    public record Team(
            List<String> players,
            List<Score> scores
    ) {}

    public record Score(
            Combination combination,
            int value
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
        CHANCE
    }
}
