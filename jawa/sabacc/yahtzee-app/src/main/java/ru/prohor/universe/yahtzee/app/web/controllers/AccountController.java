package ru.prohor.universe.yahtzee.app.web.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.app.services.AccountService;
import ru.prohor.universe.yahtzee.app.web.api.ColorInfo;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/info")
    public ResponseEntity<InfoResponse> info(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(accountService.getPlayerInfo(player.get()));
    }

    public record InfoResponse(
            String username,
            String name,
            ColorInfo color,
            @JsonProperty("image_id")
            String imageId,
            RoomInfo room, // optional
            @JsonProperty("total_incoming_requests")
            int totalIncomingRequests // show request sticker (1 - 9+)
    ) {}

    public record RoomInfo(
            String type, // lowercase RoomType
            String creation, // datetime
            int teams
    ) {}

    @PostMapping("/friends")
    public ResponseEntity<FriendsResponse> friends(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @Valid
            @RequestBody
            FriendsRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(accountService.getFriends(player.get(), body.page()));
    }

    public record FriendsRequest(
            @Min(0) @Max(1000)
            int page // starts from 0, page_size = 5 items
    ) {}

    public record FriendsResponse(
            // max 5 in response, use pagination
            List<Friend> friends,
            int page, // may differ for the requested value, starts from 0
            @JsonProperty("max_page")
            int maxPage // may differ for the requested value
    ) {}

    public record Friend(
            String id,
            String username,
            String name,
            @JsonProperty("image_id")
            String imageId,
            boolean inGame // if true - can not invite in room
    ) {}
}
