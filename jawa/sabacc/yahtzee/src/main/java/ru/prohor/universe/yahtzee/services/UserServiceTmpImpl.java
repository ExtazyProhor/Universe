package ru.prohor.universe.yahtzee.services;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.scarifJwt.AuthorizedUser;
import ru.prohor.universe.yahtzee.web.YahtzeeAuthorizedUser;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

import java.util.List;

@Service
public class UserServiceTmpImpl implements UserService {
    @Override
    public YahtzeeAuthorizedUser wrap(AuthorizedUser user) {
        return new YahtzeeAuthorizedUser(
                user.id(),
                user.uuid(),
                new ObjectId(user.objectId()),
                user.username(),
                user.username(),
                ObjectId.get(),
                "ff0000"
        );
    }

    @Override
    public boolean changeName(YahtzeeAuthorizedUser user, String name) {
        return true;
    }

    @Override
    public boolean changeColor(YahtzeeAuthorizedUser user, String color) {
        return true;
    }

    @Override
    public AccountController.FindUsersResponse findUsers(YahtzeeAuthorizedUser user, String name, int page) {
        return new AccountController.FindUsersResponse(List.of());
    }

    @Override
    public boolean addFriend(YahtzeeAuthorizedUser user, String id) {
        return true;
    }

    @Override
    public boolean deleteFriend(YahtzeeAuthorizedUser user, String id) {
        return true;
    }

    @Override
    public AccountController.FriendsResponse getFriends(YahtzeeAuthorizedUser user, int page) {
        return new AccountController.FriendsResponse(List.of());
    }
}
