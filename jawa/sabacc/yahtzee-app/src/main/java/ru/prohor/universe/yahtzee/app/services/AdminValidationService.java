package ru.prohor.universe.yahtzee.app.services;

import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

import java.util.Set;

public class AdminValidationService {
    private final Set<ObjectId> adminIds;

    public AdminValidationService(Set<ObjectId> adminIds) {
        this.adminIds = adminIds;
    }

    public boolean isAdmin(Player player) {
        return adminIds.contains(player.id());
    }
}
