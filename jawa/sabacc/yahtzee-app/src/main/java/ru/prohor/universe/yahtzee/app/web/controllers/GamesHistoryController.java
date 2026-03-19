package ru.prohor.universe.yahtzee.app.web.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.app.services.GamesHistoryService;
import ru.prohor.universe.yahtzee.core.data.GameType;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class GamesHistoryController {
    private final GamesHistoryService gamesHistoryService;

    public GamesHistoryController(GamesHistoryService gamesHistoryService) {
        this.gamesHistoryService = gamesHistoryService;
    }

    @GetMapping
    public ResponseEntity<GamesHistoryResponse> history(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestParam(value = "page", required = false, defaultValue = "0")
            int page
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return gamesHistoryService.getGamesHistory(player.get(), page);
    }

    public record GamesHistoryResponse(
            @JsonProperty("total_pages")
            int totalPages,
            @JsonProperty("current_page")
            List<GameDescription> currentPage
    ) {}

    public record GameDescription(
            ObjectId id,
            GameType type,
            String date, // datetime
            int teams,
            int players,
            int total
    ) {}
}
