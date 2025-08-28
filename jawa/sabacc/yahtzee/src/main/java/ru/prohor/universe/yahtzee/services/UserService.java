package ru.prohor.universe.yahtzee.services;

import ru.prohor.universe.jocasta.scarifJwt.AuthorizedUser;
import ru.prohor.universe.yahtzee.web.YahtzeeAuthorizedUser;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

public interface UserService {
    YahtzeeAuthorizedUser wrap(AuthorizedUser user);

    boolean changeName(YahtzeeAuthorizedUser user, String name);

    boolean changeColor(YahtzeeAuthorizedUser user, String color);

    default AccountController.InfoResponse getUserInfo(YahtzeeAuthorizedUser user) {
        return new AccountController.InfoResponse(
                user.username(),
                user.name(),
                user.color(),
                user.imageId().toHexString()
        );
    }

    // TODO not me, check who is friend
    AccountController.FindUsersResponse findUsers(YahtzeeAuthorizedUser user, String name, int page);

    boolean addFriend(YahtzeeAuthorizedUser user, String id);

    boolean deleteFriend(YahtzeeAuthorizedUser user, String id);

    AccountController.FriendsResponse getFriends(YahtzeeAuthorizedUser user, int page);
}
