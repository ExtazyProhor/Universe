package ru.prohor.universe.yahtzee.legacy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.chopper.client.ChopperClient;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.utils.ExceptionsUtils;
import ru.prohor.universe.jocasta.spring.UniverseEnvironment;
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
    private final UniverseEnvironment environment;
    private final ChopperClient chopperClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter prettyWriter = objectMapper.writer(new PrettyJsonPrinter());
    private final ZoneId utc = ZoneId.of("UTC");
    private final Path dir;
    private final long notifiableChatId;
    private final long mutedChatId;

    public SaveController(
            UniverseEnvironment environment,
            ChopperClient chopperClient,
            @Value("${universe.yahtzee.legacy.output-path}")
            String outputPath,
            @Value("${universe.yahtzee-legacy.notifiable-chat-id}")
            long notifiableChatId,
            @Value("${universe.yahtzee-legacy.muted-chat-id}")
            long mutedChatId
    ) {
        this.environment = environment;
        this.chopperClient = chopperClient;
        this.dir = Path.of(outputPath);
        if (!Files.exists(dir))
            Sneaky.execute(() -> Files.createDirectory(dir));
        this.notifiableChatId = notifiableChatId;
        this.mutedChatId = mutedChatId;
    }

    @PostMapping("/action")
    public ResponseEntity<String> action(
            @RequestParam(value = "gameId", required = false) String gameId,
            @RequestHeader(value = "X-Real-IP", required = false) String ip,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @RequestBody String jsonData
    ) {
        try {
            JsonNode node = objectMapper.readTree(jsonData);
            String type = node.get("type").asText();
            String message = switch (type) {
                case "game_start" -> "Игра началась";
                case "game_end" -> "Игра завершена принудительно";
                case "undo" -> {
                    String teamName = node.has("teamName") && !node.get("teamName").isNull()
                            ? node.get("teamName").asText() : "?";
                    yield "Отмена хода у команды '" + teamName + "'";
                }
                default -> {
                    chopperClient.sendMessage(
                            "Illegal action type: " + type,
                            notifiableChatId
                    );
                    yield "Неизвестное действие: " + type;
                }
            };

            chopperClient.sendMessage(appendMeta(message, gameId, ip, userAgent), mutedChatId);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            Sneaky.silent(() -> error(e, gameId, ip, userAgent));
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/combination")
    public ResponseEntity<String> combination(
            @RequestParam(value = "gameId", required = false) String gameId,
            @RequestHeader(value = "X-Real-IP", required = false) String ip,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @RequestBody String jsonData
    ) {
        try {
            JsonNode node = objectMapper.readTree(jsonData);
            String teamName = node.get("teamName").asText();
            String combination = node.get("combination").asText();
            int score = node.get("score").asInt();

            String message = appendMeta(
                    teamName + " → " + resolveCombination(combination) + ": " + score,
                    gameId,
                    ip,
                    userAgent
            );
            chopperClient.sendMessage(message, mutedChatId);

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            Sneaky.silent(() -> error(e, gameId, ip, userAgent));
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    private String resolveCombination(String combination) {
        return switch (combination) {
            case "units" -> "единицы";
            case "twos" -> "двойки";
            case "threes" -> "тройки";
            case "fours" -> "четверки";
            case "fives" -> "пятерки";
            case "sixes" -> "шестерки";

            case "pair" -> "пара";
            case "two-pairs" -> "две пары";
            case "three-of-kind" -> "сет";
            case "four-of-kind" -> "каре";
            case "full-house" -> "фулл хаус";
            case "small-straight" -> "малый стрит";
            case "large-straight" -> "большой стрит";
            case "yahtzee" -> "яцзы";
            case "chance" -> "шанс";
            default -> {
                chopperClient.sendMessage(
                        "Illegal name of combination: " + combination,
                        notifiableChatId
                );
                yield combination;
            }
        };
    }

    @PostMapping("/game")
    public ResponseEntity<String> game(
            @RequestParam(value = "gameId", required = false) String gameId,
            @RequestHeader(value = "X-Real-IP", required = false) String ip,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @RequestBody String jsonData
    ) {
        try {
            LocalDateTime now = LocalDateTime.now(utc);
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            Files.writeString(
                    dir.resolve("game_" + now.format(formatter) + ".json"),
                    prettyWriter.writeValueAsString(jsonNode)
            );
            notify(jsonNode, gameId, ip, userAgent);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            Sneaky.silent(() -> error(e, gameId, ip, userAgent));
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    private String appendMeta(String message, String gameId, String ip, String userAgent) {
        return message +
                "\n\n" +
                "game: " +
                gameId +
                "\nip: " +
                (environment.canBeObtainedLocally() ? "local env" : ip) +
                "\nuser agent: " +
                userAgent;
    }

    private void error(Exception e, String gameId, String ip, String userAgent) {
        chopperClient.sendFile(
                ExceptionsUtils.getStackTraceAsString(e),
                notifiableChatId,
                "stack-trace.txt",
                appendMeta("New exception at legacy yahtzee", gameId, ip, userAgent),
                false
        );
    }

    private void notify(JsonNode jsonNode, String gameId, String ip, String userAgent) {
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
        chopperClient.sendMessage(appendMeta(builder.toString(), gameId, ip, userAgent), notifiableChatId);
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
