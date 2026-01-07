package ru.prohor.universe.yahtzee.app.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.yahtzee.app.services.OfflineGameInfoService;

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

    public record OfflineGameInfoResponse() {}
}
