package ru.prohor.universe.yahtzee.web.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.services.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final GameColorsService gameColorsService;
    private final AccountService accountService;

    public AccountController(
            GameColorsService gameColorsService,
            AccountService accountService
    ) {
        this.gameColorsService = gameColorsService;
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
            String color, // format: "ff0000", hex, without alpha
            @JsonProperty("image_id")
            String imageId
    ) {}

    @PostMapping("/change_name")
    public ResponseEntity<?> changeName(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @Valid
            @RequestBody
            ChangeNameRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (accountService.changeName(player.get(), body.name()))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    public record ChangeNameRequest(
            @NotNull
            @Size(min = 3, max = 20)
            @Pattern(regexp = "^\\S+$")
            String name
    ) {}

    @GetMapping("/get_available_colors")
    public ResponseEntity<AvailableColorsResponse> getAvailableColors() {
        return ResponseEntity.ok(gameColorsService.getAvailableColors());
    }

    public record AvailableColorsResponse(
            List<AvailableColor> colors
    ) {}

    public record AvailableColor(
            @JsonProperty("color_id")
            int colorId,
            // format: "ff0000", hex, without alpha
            String color
    ) {}

    @PostMapping("/change_preferred_color")
    public ResponseEntity<?> changePreferredColor(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestBody
            ChangePreferredColorRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (accountService.changeColor(player.get(), body.colorId()))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    public record ChangePreferredColorRequest(
            @JsonProperty("color_id")
            int colorId
    ) {}

    @PostMapping("/find_users")
    public ResponseEntity<FindUsersResponse> findUser(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @Valid
            @RequestBody
            FindUsersRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(accountService.findUsers(player.get(), body.name(), body.page()));
    }

    public record FindUsersRequest(
            String name,
            @Min(0) @Max(1000)
            int page // starts from 0, page_size = 5 items
    ) {}

    public record FindUsersResponse(
            // max 5 in response, use pagination
            @JsonProperty("found_users")
            List<FoundUser> foundUsers,
            long total
    ) {}

    public record FoundUser(
            String id,
            String username,
            String name,
            @JsonProperty("image_id")
            String imageId,
            @JsonProperty("is_friend")
            boolean isFriend
    ) {}

    @PostMapping("/add_friend")
    public ResponseEntity<?> addFriend(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestBody
            AddFriendRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (accountService.addFriend(player.get(), body.id()))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    public record AddFriendRequest(
            String id
    ) {}

    @PostMapping("/delete_friend")
    public ResponseEntity<?> deleteFriend(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestBody
            DeleteFriendRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (accountService.deleteFriend(player.get(), body.id()))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    public record DeleteFriendRequest(
            String id
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
            long total
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
