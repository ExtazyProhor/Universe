package ru.prohor.universe.yahtzee.legacy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.spring.features.PrettyJsonPrinter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mobile")
public class MobileController {
    private final Path dir;
    private final File userKeys;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter prettyWriter = objectMapper.writer(new PrettyJsonPrinter());

    public MobileController(
            @Value("${universe.yahtzee.legacy.mobile-output-path}")
            String outputPath,
            @Value("${universe.yahtzee.legacy.user-keys}")
            String userKeys
    ) {
        this.dir = Path.of(outputPath);
        if (!Files.exists(dir))
            Sneaky.execute(() -> dir.toFile().mkdirs());
        this.userKeys = new File(userKeys);
        if (!this.userKeys.exists()) {
            throw new IllegalStateException("User keys file does not exists: " + userKeys);
        }
    }

    private boolean validateKey(String userKey) {
        UserKeysConfig config = Sneaky.execute(() -> objectMapper.readValue(userKeys, UserKeysConfig.class));
        return config.keys.stream().anyMatch(key -> key.key.equals(userKey));
    }

    private record UserKeysConfig(
            List<UserKey> keys
    ) {}

    private record UserKey(
            String key,
            String name
    ) {}

    private void saveGame(String game, String userKey) {
        Sneaky.execute(() -> {
            JsonNode node = objectMapper.readTree(game);
            ((ObjectNode) node).put("user-key", userKey);
            Files.writeString(
                    dir.resolve(UUID.randomUUID() + ".json"),
                    prettyWriter.writeValueAsString(node)
            );
        });
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(
            @RequestHeader("X-User-Key") String userKey
    ) {
        if (validateKey(userKey)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/game")
    public ResponseEntity<Void> game(
            @RequestHeader("X-User-Key") String userKey,
            @RequestBody String game
    ) {
        if (!validateKey(userKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        saveGame(game, userKey);
        return ResponseEntity.ok().build();
    }
}
