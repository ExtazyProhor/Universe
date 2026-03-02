package ru.prohor.universe.yahtzee.legacy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.chopper.client.ChopperClient;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.utils.ExceptionsUtils;
import ru.prohor.universe.jocasta.spring.features.PrettyJsonPrinter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SaveController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final ChopperClient chopperClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter prettyWriter = objectMapper.writer(new PrettyJsonPrinter());
    private final ZoneId utc = ZoneId.of("UTC");
    private final Path dir;
    private final long notifiableChatId;

    public SaveController(
            ChopperClient chopperClient,
            @Value("${universe.yahtzee.legacy.output-path}")
            String outputPath,
            @Value("${universe.yahtzee-legacy.notifiable-chat-id}")
            long notifiableChatId
    ) {
        this.chopperClient = chopperClient;
        this.dir = Path.of(outputPath);
        if (!Files.exists(dir))
            Sneaky.execute(() -> Files.createDirectory(dir));
        this.notifiableChatId = notifiableChatId;
    }

    @PostMapping("/game")
    public ResponseEntity<String> game(@RequestBody String jsonData) {
        try {
            LocalDateTime now = LocalDateTime.now(utc);
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            Files.writeString(
                    dir.resolve("game_" + now.format(formatter) + ".json"),
                    prettyWriter.writeValueAsString(jsonNode)
            );
            notify(jsonNode);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            Sneaky.silent(() -> error(e));
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    private void error(Exception e) {
        chopperClient.sendFile(
                ExceptionsUtils.getStackTraceAsString(e),
                notifiableChatId,
                "stack-trace.txt",
                "New exception at legacy yahtzee",
                false
        );
    }

    private void notify(JsonNode jsonNode) {
        Map<String, List<Combination>> teams = new HashMap<>();
        jsonNode.forEach(node -> {
            String team = node.get("teamName").asText();
            List<Combination> list = Opt.ofNullable(teams.get(team)).orElseGet(ArrayList::new);
            teams.put(team, list);
            list.add(new Combination(
                    node.get("combination").asText(),
                    node.get("score").asInt()
            ));
        });
        Map<String, Integer> totals = teams.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> calculateTotal(entry.getValue())
        ));
        StringBuilder builder = new StringBuilder("Новая игра в Яцзы сохранена!\n\n");
        totals.forEach((team, total) -> builder.append("- ").append(team).append(": ").append(total).append("\n"));
        chopperClient.sendMessage(builder.toString(), notifiableChatId, false);
    }

    private int calculateTotal(List<Combination> combinations) {
        int bonus = combinations.stream()
                .filter(this::isSimple)
                .mapToInt(Combination::value)
                .sum() >= 63 ? 35 : 0;
        return bonus + combinations.stream().mapToInt(Combination::value).sum();
    }

    private final Set<String> simples = new HashSet<>(Set.of("units", "twos", "threes", "fours", "fives", "sixes"));

    private boolean isSimple(Combination combination) {
        return simples.contains(combination.name);
    }

    private record Combination(
            String name,
            int value
    ) {}
}
