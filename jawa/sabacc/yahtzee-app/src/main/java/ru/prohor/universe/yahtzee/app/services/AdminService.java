package ru.prohor.universe.yahtzee.app.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.prohor.universe.jocasta.jackson.morphia.MongoForceBackupService;
import ru.prohor.universe.yahtzee.app.web.controllers.AdminPanelController;
import ru.prohor.universe.yahtzee.core.color.TeamColor;
import ru.prohor.universe.yahtzee.core.color.YahtzeeColor;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminService {
    private final MongoForceBackupService mongoForceBackupService;
    private final AdminValidationService adminValidationService;

    public AdminService(
            MongoForceBackupService mongoForceBackupService,
            AdminValidationService adminValidationService
    ) {
        this.mongoForceBackupService = mongoForceBackupService;
        this.adminValidationService = adminValidationService;
    }

    public ResponseEntity<List<AdminPanelController.ColorProperties>> getAvailableColors(Player player) {
        if (!adminValidationService.isAdmin(player)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                Arrays.stream(YahtzeeColor.values())
                        .map(this::mapYahtzeeColor)
                        .toList()
        );
    }

    private AdminPanelController.ColorProperties mapYahtzeeColor(YahtzeeColor color) {
        TeamColor teamColor = color.getTeamColor();
        return new AdminPanelController.ColorProperties(
                color.getColorId(),
                color.name(),
                teamColor.background(),
                teamColor.text(),
                teamColor.light(),
                teamColor.dark()
        );
    }

    public ResponseEntity<ByteArrayResource> getBackup(Player player) {
        if (!adminValidationService.isAdmin(player)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] data = mongoForceBackupService.backupAsJson().getBytes();
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=backup.json")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(data.length)
                .body(resource);
    }

    public ResponseEntity<Void> recoveryBackup(Player player, MultipartFile file) {
        if (!adminValidationService.isAdmin(player)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            mongoForceBackupService.recovery(file.getInputStream());
        } catch (IOException e) {
            // todo log e
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }
}
