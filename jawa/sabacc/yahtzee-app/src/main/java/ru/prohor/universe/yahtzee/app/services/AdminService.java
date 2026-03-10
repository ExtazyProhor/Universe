package ru.prohor.universe.yahtzee.app.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.prohor.universe.jocasta.jackson.morphia.MongoForceBackupService;
import ru.prohor.universe.yahtzee.core.core.color.TeamColor;
import ru.prohor.universe.yahtzee.core.data.pojo.Player;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;

import java.io.IOException;
import java.util.List;

@Service
public class AdminService {
    private final MongoForceBackupService mongoForceBackupService;
    private final AdminValidationService adminValidationService;
    private final GameColorsService gameColorsService;

    public AdminService(
            MongoForceBackupService mongoForceBackupService,
            AdminValidationService adminValidationService,
            GameColorsService gameColorsService
    ) {
        this.mongoForceBackupService = mongoForceBackupService;
        this.adminValidationService = adminValidationService;
        this.gameColorsService = gameColorsService;
    }

    public ResponseEntity<List<TeamColor>> getAvailableColors(Player player) {
        if (!adminValidationService.isAdmin(player)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(gameColorsService.getAvailableColors());
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

    public ResponseEntity<?> recoveryBackup(Player player, MultipartFile file) {
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
