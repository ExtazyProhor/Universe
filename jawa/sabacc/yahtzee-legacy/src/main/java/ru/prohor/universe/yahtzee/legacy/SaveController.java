package ru.prohor.universe.yahtzee.legacy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class SaveController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path dir;

    public SaveController(
            @Value("${universe.yahtzee.legacy.output-path}")
            String outputPath
    ) {
        this.dir = Path.of(outputPath);
    }

    @PostMapping("/game")
    public ResponseEntity<String> game(@RequestBody String jsonData) {
        try {
            objectMapper.readTree(jsonData);
            LocalDateTime now = LocalDateTime.now();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            Files.writeString(
                    dir.resolve("game_" + now.format(formatter) + ".json"),
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
            );
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
