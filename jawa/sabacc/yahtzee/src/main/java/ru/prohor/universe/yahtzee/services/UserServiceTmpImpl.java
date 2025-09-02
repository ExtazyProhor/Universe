package ru.prohor.universe.yahtzee.services;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.scarifJwt.AuthorizedUser;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

import java.util.List;

@Service
public class UserServiceTmpImpl implements UserService {
    @Override
    public Player wrap(AuthorizedUser user) {
        return new Player(
                new ObjectId(user.objectId()),
                user.uuid(),
                user.id(),
                user.username(),
                "ff0000",
                user.username(),
                List.of(),
                Opt.of(ObjectId.get()),
                ObjectId.get(),
                Instant.now(),
                false
        );
    }

    @Override
    public boolean changeName(Player user, String name) {
        return true;
    }

    @Override
    public boolean changeColor(Player user, String color) {
        return true;
    }

    @Override
    public AccountController.FindUsersResponse findUsers(Player user, String name, int page) {
        return new AccountController.FindUsersResponse(List.of());
    }

    @Override
    public boolean addFriend(Player user, String id) {
        return true;
    }

    @Override
    public boolean deleteFriend(Player user, String id) {
        return true;
    }

    @Override
    public AccountController.FriendsResponse getFriends(Player user, int page) {
        return new AccountController.FriendsResponse(List.of());
    }
}
