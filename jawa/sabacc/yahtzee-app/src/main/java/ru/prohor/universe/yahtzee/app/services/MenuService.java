package ru.prohor.universe.yahtzee.app.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.yahtzee.app.web.api.ColorInfo;
import ru.prohor.universe.yahtzee.app.web.api.GeneralRoomInfo;
import ru.prohor.universe.yahtzee.app.web.api.MenuInitialResponse;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;

@Service
public class MenuService {
    private final NotificationService notificationService;
    private final GameColorsService gameColorsService;

    public MenuService(NotificationService notificationService, GameColorsService gameColorsService) {
        this.notificationService = notificationService;
        this.gameColorsService = gameColorsService;
    }

    public ResponseEntity<MenuInitialResponse> getResponse(Player player) {
        return ResponseEntity.ok(new MenuInitialResponse(
                player.displayName(),
                player.username(),
                ColorInfo.of(gameColorsService.getById(player.color())),
                player.imageId().toString(),
                player.currentRoom().map(room -> new GeneralRoomInfo(
                        room.getType(),
                        DateTimeUtil.toReadableString(room.getCreatedAt()),
                        room.getTeamsCount(),
                        room.getPlayersCount()
                )).orElseNull(),
                notificationService.hasNotifications(player)
        ));
    }
}
