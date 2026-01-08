package ru.prohor.universe.yahtzee.app.web.controllers;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.yahtzee.app.services.OfflineGameInfoService;
import ru.prohor.universe.yahtzee.app.web.api.ColorInfo;
import ru.prohor.universe.yahtzee.core.core.Combination;

import java.util.List;

@RestController
@RequestMapping("/api/offline/game-info")
public class OfflineGameInfoController {
    private final OfflineGameInfoService offlineGameInfoService;

    public OfflineGameInfoController(OfflineGameInfoService offlineGameInfoService) {
        this.offlineGameInfoService = offlineGameInfoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfflineGameInfoResponse> image(@PathVariable("id") String gameId) {
        return offlineGameInfoService.getOfflineGameInfo(gameId);
    }

    public record OfflineGameInfoResponse(
            ObjectId id,
            String date,
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
