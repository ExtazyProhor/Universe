package ru.prohor.universe.yahtzee.services.color;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.yahtzee.core.TeamColor;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameColorsService {
    // TODO цвета в enum
    // TODO тесты на выбор цвета
    private static final Map<Integer, TeamColor> COLORS = Stream.of(
            new TeamColor(0, "DC143C", "FFFFFF", "FFE6E6", "8B0000"),
            new TeamColor(1, "FF4500", "FFFFFF", "FFE4E1", "CC3700"),
            new TeamColor(2, "FF8C00", "FFFFFF", "FFE4B5", "CC7000"),
            new TeamColor(3, "FFD700", "000000", "FFF8DC", "B8860B"),
            new TeamColor(4, "32CD32", "FFFFFF", "F0FFF0", "228B22"),
            new TeamColor(5, "0C610C", "FFFFFF", "EAF6EA", "0F3D0F"),
            new TeamColor(6, "20B2AA", "FFFFFF", "E0FFFF", "008B8B"),
            new TeamColor(7, "00BFFF", "FFFFFF", "F0F8FF", "0080CC"),
            new TeamColor(8, "1E70FF", "FFFFFF", "E6F2FF", "104E8B"),
            new TeamColor(9, "FF69B4", "FFFFFF", "FFE4E1", "CC5490"),
            new TeamColor(10, "9370DB", "FFFFFF", "E6E6FA", "663399"),
            new TeamColor(11, "630363", "FFFFFF", "F5E6F5", "3B003B")
    ).collect(Collectors.toMap(TeamColor::colorId, color -> color));

    private static final int[] COLOR_IDS = COLORS.keySet().stream().mapToInt(i -> i).toArray();

    private final Random random;

    public GameColorsService(
            @Value("${universe.yahtzee.game.offline.max-teams}") int maxTeams
    ) {
        checkTeamsSize(maxTeams);
        this.random = new Random();
    }

    public AccountController.AvailableColorsResponse getAvailableColors() {
        return new AccountController.AvailableColorsResponse(
                COLORS.values().stream().map(
                        color -> new AccountController.AvailableColor(color.colorId(), color.background())
                ).toList()
        );
    }

    public int getRandomColorId() {
        return COLORS.get(COLOR_IDS[random.nextInt(COLOR_IDS.length)]).colorId();
    }

    public boolean validateColor(int colorId) {
        return COLORS.containsKey(colorId);
    }

    public TeamColor getById(int colorId) {
        return COLORS.get(colorId);
    }

    public Map<String, TeamColor> calculateColorsForTeams(Map<String, List<Player>> teams) {
        int n = teams.size();
        int m = COLORS.size();
        checkTeamsSize(n);

        List<String> teamNames = new ArrayList<>(teams.keySet());
        int[][] score = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int colorId = COLORS.get(j).colorId();
                score[i][j] = (int) teams.get(teamNames.get(i))
                        .stream()
                        .filter(p -> colorId == p.color())
                        .count();
            }
        }

        int maxScore = Arrays.stream(score).flatMapToInt(Arrays::stream).max().orElse(0);
        int[][] costMatrix = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                costMatrix[i][j] = maxScore - score[i][j];

        HungarianAlgorithm hungarian = new HungarianAlgorithm(costMatrix);
        int[] assignment = hungarian.execute();
        Map<String, TeamColor> result = new HashMap<>();
        for (int i = 0; i < n; i++)
            if (assignment[i] >= 0)
                result.put(teamNames.get(i), COLORS.get(assignment[i]));
        return result;
    }

    private void checkTeamsSize(int teamSize) {
        if (COLORS.size() < teamSize)
            throw new RuntimeException("There are more teams than available colors");
    }
}
