package ru.prohor.universe.yahtzee.web.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.yahtzee.web.YahtzeeAuthorizedUser;

import java.util.List;

@RestController("/api/account")
public class AccountController {

    @PostMapping("/info")
    public ResponseEntity<InfoResponse> info(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user
    ) {
        return ResponseEntity.badRequest().build(); // TODO
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
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record ChangeNameRequest(
            String name
    ) {}

    @PostMapping("/change_preferred_color")
    public ResponseEntity<?> changePreferredColor(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user,
            @RequestBody
            ChangePreferredColorRequest body
    ) {
        return ResponseEntity.badRequest().build(); // TODO
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
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record FindUsersRequest(
            String name
    ) {}

    public record FindUsersResponse(
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
        return ResponseEntity.badRequest().build(); // TODO
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
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record DeleteFriendRequest(
            String id
    ) {}

    @PostMapping("/friends")
    public ResponseEntity<FriendsResponse> friends(
            @RequestAttribute(YahtzeeAuthorizedUser.ATTRIBUTE_KEY)
            Opt<YahtzeeAuthorizedUser> user
    ) {
        return ResponseEntity.badRequest().build(); // TODO
    }

    public record FriendsResponse(
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
