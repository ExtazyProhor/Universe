package ru.prohor.universe.yahtzee.app.web.controllers;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.app.services.AdminService;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminPanelController {
    private final AdminService adminService;

    public AdminPanelController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/colors")
    public ResponseEntity<List<ColorProperties>> getAvailableColors(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return adminService.getAvailableColors(player.get());
    }

    public record ColorProperties(
            int colorId,
            String colorName,
            String background,
            String text,
            String light,
            String dark
    ) {}

    @GetMapping("/backup")
    public ResponseEntity<ByteArrayResource> backup(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return adminService.getBackup(player.get());
    }

    @PostMapping(
            value = "/recovery",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> recovery(
            @RequestAttribute(Player.ATTRIBUTE_KEY)
            Opt<Player> player,
            @RequestParam("file")
            MultipartFile file
    ) {
        if (player.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return adminService.recoveryBackup(player.get(), file);
    }
}
