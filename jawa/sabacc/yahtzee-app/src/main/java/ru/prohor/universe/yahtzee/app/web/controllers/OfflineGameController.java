package ru.prohor.universe.yahtzee.app.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.offline.api.CreateRoomRequest;
import ru.prohor.universe.yahtzee.offline.api.RoomInfoResponse;
import ru.prohor.universe.yahtzee.offline.api.SaveMoveRequest;
import ru.prohor.universe.yahtzee.offline.services.OfflineGameService;

@RestController
@RequestMapping("/api/game/offline")
public class OfflineGameController {
    private final OfflineGameService offlineGameService;

    public OfflineGameController(OfflineGameService offlineGameService) {
        this.offlineGameService = offlineGameService;
    }

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

    @PostMapping("/create_room")
    public ResponseEntity<?> createRoom(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestBody
            CreateRoomRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return offlineGameService.createRoom(player.get(), body);
    }

    @PostMapping("/save_move")
    public ResponseEntity<?> saveMove(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestBody
            SaveMoveRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return offlineGameService.saveMove(player.get(), body);
    }
}
