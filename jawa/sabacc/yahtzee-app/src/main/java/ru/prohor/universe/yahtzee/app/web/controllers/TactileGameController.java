package ru.prohor.universe.yahtzee.app.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.app.services.TactileGameService;
import ru.prohor.universe.yahtzee.app.web.api.SaveMoveRequest;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

@RestController
@RequestMapping("/api/game/tactile")
public class TactileGameController {
    private final TactileGameService tactileGameService;

    public TactileGameController(TactileGameService tactileGameService) {
        this.tactileGameService = tactileGameService;
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
        return tactileGameService.saveMove(player.get(), body);
    }
}
