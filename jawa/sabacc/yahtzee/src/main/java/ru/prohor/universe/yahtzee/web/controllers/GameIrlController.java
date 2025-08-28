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

    @PostMapping("/players_info")
    public ResponseEntity<PlayersInfoResponse> getPlayersInfo(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            PlayersInfoRequest body
    ) {
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record PlayersInfoRequest(
            @JsonProperty("player_ids")
            List<String> playerIds
    ) {}

    public record PlayersInfoResponse(
            List<PlayerInfo> players
    ) {}

    public record PlayerInfo(
            String id,
            String name,
            String color
    ) {}

    @PostMapping("/save")
    public ResponseEntity<?> saveGame(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            SaveGameRequest body
    ) {
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record SaveGameRequest(
            List<Team> teams
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
