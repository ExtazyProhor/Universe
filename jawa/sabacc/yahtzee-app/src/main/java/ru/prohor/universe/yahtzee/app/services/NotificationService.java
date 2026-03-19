package ru.prohor.universe.yahtzee.app.services;

import org.springframework.stereotype.Service;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

@Service
public class NotificationService {
    public boolean hasNotifications(Player player) {
        return !player.incomingRequests().isEmpty();
    }
}
