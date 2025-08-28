package ru.prohor.universe.yahtzee.services;

import ru.prohor.universe.jocasta.scarifJwt.AuthorizedUser;
import ru.prohor.universe.yahtzee.web.YahtzeeAuthorizedUser;

public interface UserService {
    YahtzeeAuthorizedUser wrap(AuthorizedUser user);
}
