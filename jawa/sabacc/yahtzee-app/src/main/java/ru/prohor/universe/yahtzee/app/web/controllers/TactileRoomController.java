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
import ru.prohor.universe.yahtzee.app.services.TactileGameService;
import ru.prohor.universe.yahtzee.app.web.api.CreateRoomRequest;
import ru.prohor.universe.yahtzee.app.web.api.RoomInfoResponse;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

@RestController
@RequestMapping("/api/room/tactile")
public class TactileRoomController {
    private final TactileGameService tactileGameService;

    public TactileRoomController(TactileGameService tactileGameService) {
        this.tactileGameService = tactileGameService;
    }

    @GetMapping("/info")
    public ResponseEntity<RoomInfoResponse> getRoomInfo(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return tactileGameService.getRoomInfo(player.get()).map(
                ResponseEntity::ok
        ).orElseGet(
                () -> ResponseEntity.notFound().build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestBody
            CreateRoomRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return tactileGameService.createRoom(player.get(), body);
    }
}
