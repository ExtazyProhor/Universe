package ru.prohor.universe.yahtzee.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/account")
public class AccountController {
    @PostMapping("/info")
    public ResponseEntity<?> info() {
        return ResponseEntity.badRequest().build(); // TODO
    }

    @PostMapping("/changeName")
    public ResponseEntity<?> changeName() {
        return ResponseEntity.badRequest().build(); // TODO
    }

    @PostMapping("/changePreferredColor")
    public ResponseEntity<?> changePreferredColor() {
        return ResponseEntity.badRequest().build(); // TODO
    }

    @PostMapping("/findUsers")
    public ResponseEntity<?> findUser() {
        return ResponseEntity.badRequest().build(); // TODO
    }

    @PostMapping("/addFriend")
    public ResponseEntity<?> addFriend() {
        return ResponseEntity.badRequest().build(); // TODO
    }

    @PostMapping("/deleteFriend")
    public ResponseEntity<?> deleteFriend() {
        return ResponseEntity.badRequest().build(); // TODO
    }

    @PostMapping("/friends")
    public ResponseEntity<?> friends() {
        return ResponseEntity.badRequest().build(); // TODO
    }
}
