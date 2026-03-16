package ru.prohor.universe.yahtzee.app.services;

import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

public interface AdminValidationService {
    boolean isAdmin(Player player);
}
