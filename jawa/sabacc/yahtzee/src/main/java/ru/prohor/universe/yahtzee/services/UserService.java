package ru.prohor.universe.yahtzee.services;

import ru.prohor.universe.jocasta.scarifJwt.AuthorizedUser;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

public interface UserService {
    Player wrap(AuthorizedUser user);

    boolean changeName(Player user, String name);

    boolean changeColor(Player user, String color);

    default AccountController.InfoResponse getUserInfo(Player user) {
        return new AccountController.InfoResponse(
                user.username(),
                user.displayName(),
                user.color(),
                user.imageId().toHexString()
        );
    }

    // TODO not me, check who is friend
    AccountController.FindUsersResponse findUsers(Player user, String name, int page);

    boolean addFriend(Player user, String id);

    boolean deleteFriend(Player user, String id);

    AccountController.FriendsResponse getFriends(Player user, int page);
}
