package ru.prohor.universe.yahtzee.app.web.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.app.services.AccountService;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final GameColorsService gameColorsService;
    private final AccountService accountService;

    public ProfileController(
            GameColorsService gameColorsService,
            AccountService accountService
    ) {
        this.gameColorsService = gameColorsService;
        this.accountService = accountService;
    }

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
        return ResponseEntity.ok(
                new AvailableColorsResponse(
                        gameColorsService.getAvailableColors().stream().map(
                                color -> new AvailableColor(color.colorId(), color.background())
                        ).toList()
                )
        );
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
            @NotNull
            @Size(min = 3, max = 20)
            @Pattern(regexp = "^\\S+$")
            String name,
            @Min(0) @Max(1000)
            int page // starts from 0, page_size = 5 items
    ) {}

    public record FindUsersResponse(
            // max 5 in response, use pagination
            @JsonProperty("found_users")
            List<FoundUser> foundUsers,
            int page, // may differ for the requested value, starts from 0
            @JsonProperty("max_page")
            int maxPage // may differ for the requested value
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

    @PostMapping("/delete_friend")
    public ResponseEntity<?> deleteFriend(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestParam("id")
            String id
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return accountService.deleteFriend(player.get(), id);
    }

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

    @PostMapping("/requests")
    public ResponseEntity<RequestsResponse> requests(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @Valid
            @RequestBody
            RequestsRequest body
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(accountService.findRequests(player.get(), body));
    }

    public record RequestsRequest(
            boolean incoming,
            @Min(0) @Max(1000)
            int page // starts from 0, page_size = 5 items
    ) {}

    public record RequestsResponse(
            // max 5 in response, use pagination
            List<PlayerRequestInfo> players,
            int page, // may differ for the requested value, starts from 0
            @JsonProperty("max_page")
            int maxPage // may differ for the requested value
    ) {}

    public record PlayerRequestInfo(
            String id,
            String username,
            String name,
            @JsonProperty("image_id")
            String imageId
    ) {}

    public record SendRequestConflict(String code) {}

    public record SendRequestResult(String result) {}

    public static final ResponseEntity<?> REQUEST_ALREADY_EXISTS = ResponseEntity.status(HttpStatus.CONFLICT).body(
            new SendRequestConflict("request_already_exists")
    );
    public static final ResponseEntity<?> ALREADY_FRIENDS = ResponseEntity.status(HttpStatus.CONFLICT).body(
            new SendRequestConflict("already_friends")
    );
    public static final ResponseEntity<?> FRIEND_ADDED = ResponseEntity.ok(
            new SendRequestResult("friend_added")
    );
    public static final ResponseEntity<?> REQUEST_SENT = ResponseEntity.ok(
            new SendRequestResult("request_sent")
    );

    @PostMapping("/send_request")
    public ResponseEntity<?> sendRequest(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestParam("id")
            String id
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return accountService.sendRequest(player.get(), id);
    }

    /**
     * On behalf of the sender
     */
    @PostMapping("/retract_request")
    public ResponseEntity<?> retractRequest(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestParam("id")
            String id
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return accountService.retractRequest(player.get(), id);
    }

    /**
     * On behalf of the recipient
     */
    @PostMapping("/decline_request")
    public ResponseEntity<?> declineRequest(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestParam("id")
            String id
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return accountService.declineRequest(player.get(), id);

    }

    @PostMapping("/accept_request")
    public ResponseEntity<?> acceptRequest(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestParam("id")
            String id
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return accountService.acceptRequest(player.get(), id);
    }
}
