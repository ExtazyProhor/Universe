package ru.prohor.universe.yahtzee.app.web.controllers;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.yahtzee.app.services.TactileGameInfoService;
import ru.prohor.universe.yahtzee.app.web.api.ColorInfo;
import ru.prohor.universe.yahtzee.core.data.Combination;
import ru.prohor.universe.yahtzee.core.data.GameType;

import java.util.List;

@RestController
@RequestMapping("/api/game-info")
public class GameInfoController {
    private final TactileGameInfoService tactileGameInfoService;

    public GameInfoController(TactileGameInfoService tactileGameInfoService) {
        this.tactileGameInfoService = tactileGameInfoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameInfoResponse> tactileGameInfo(@PathVariable("id") String gameId) {
        return tactileGameInfoService.getTactileGameInfo(gameId);
    }

    public record GameInfoResponse(
            ObjectId id,
            String date,
            GameType type,
            PlayerInfoResponse initiator,
            List<TeamInfoResponse> teams,
            boolean trusted
    ) {}

    public record TeamInfoResponse(
            List<PlayerInfoResponse> players,
            List<ScoreInfoResponse> scores, // nullable
            int total,
            Boolean hasBonus // nullable
    ) {}

    public record PlayerInfoResponse(
            ObjectId id,
            String username,
            ColorInfo color,
            String displayName,
            ObjectId imageId
    ) {}

    public record ScoreInfoResponse(
            Combination combination,
            int value
    ) {}
}
