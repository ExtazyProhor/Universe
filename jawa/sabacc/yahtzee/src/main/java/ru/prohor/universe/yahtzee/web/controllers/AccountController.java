package ru.prohor.universe.yahtzee.web.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.yahtzee.services.GameColorsService;
import ru.prohor.universe.yahtzee.services.UserService;
import ru.prohor.universe.yahtzee.web.YahtzeeAuthorizedUser;

import java.util.List;

@RestController("/api/account")
public class AccountController {
    private final GameColorsService gameColorsService;
    private final UserService userService;

    public AccountController(
            GameColorsService gameColorsService,
            UserService userService
    ) {
        this.gameColorsService = gameColorsService;
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<InfoResponse> info(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user
    ) {
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(userService.getUserInfo(user.get()));
    }

    public record InfoResponse(
            String username,
            String name,
            String color,
            @JsonProperty("image_id")
            String imageId
    ) {}

    @PostMapping("/change_name")
    public ResponseEntity<?> changeName(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            ChangeNameRequest body
    ) {
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (userService.changeName(user.get(), body.name()))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    public record ChangeNameRequest(
            String name
    ) {}

    @GetMapping("/get_available_colors")
    public ResponseEntity<AvailableColorsResponse> getAvailableColors() {
        return ResponseEntity.ok(gameColorsService.getAvailableColors());
    }

    public record AvailableColorsResponse(
            // format: "ff0000", hex, without alpha
            List<String> colors
    ) {}

    @PostMapping("/change_preferred_color")
    public ResponseEntity<?> changePreferredColor(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            ChangePreferredColorRequest body
    ) {
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (userService.changeColor(user.get(), body.color()))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    public record ChangePreferredColorRequest(
            String color
    ) {}

    @PostMapping("/find_users")
    public ResponseEntity<FindUsersResponse> findUser(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            FindUsersRequest body
    ) {
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(userService.findUsers(user.get(), body.name(), body.page()));
    }

    public record FindUsersRequest(
            String name,
            int page
    ) {}

    public record FindUsersResponse(
            // max 10 in response, use pagination
            @JsonProperty("found_users")
            List<FoundUser> foundUsers
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
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            AddFriendRequest body
    ) {
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (userService.addFriend(user.get(), body.id()))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    public record AddFriendRequest(
            String id
    ) {}

    @PostMapping("/delete_friend")
    public ResponseEntity<?> deleteFriend(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            DeleteFriendRequest body
    ) {
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (userService.deleteFriend(user.get(), body.id()))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    public record DeleteFriendRequest(
            String id
    ) {}

    @PostMapping("/friends")
    public ResponseEntity<FriendsResponse> friends(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            FriendsRequest body
    ) {
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(userService.getFriends(user.get(), body.page()));
    }

    public record FriendsRequest(
            int page
    ) {}

    public record FriendsResponse(
            // max 10 in response, use pagination
            List<Friend> friends
    ) {}

    public record Friend(
            String id,
            String username,
            String name,
            @JsonProperty("image_id")
            String imageId
    ) {}
}
