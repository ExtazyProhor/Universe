package ru.prohor.universe.yahtzee.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/game/irl")
public class GameIrlController {
    @PostMapping("/save")
    public ResponseEntity<?> saveGame() {
        return ResponseEntity.badRequest().build(); // TODO
    }
}
