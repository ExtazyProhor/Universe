package ru.prohor.universe.yahtzee.services.color;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameColorsService {
    // TODO цвета в enum
    // TODO тесты на выбор цвета
    private static final List<String> AVAILABLE_COLORS = List.of(); // TODO
    // TODO цвета это не одно значение, еще нужен светлый вариант и цвет текста

    public GameColorsService(
            @Value("${universe.yahtzee.game.irl.max-teams}") int maxTeams
    ) {
        checkTeamsSize(maxTeams, AVAILABLE_COLORS.size());
    }

    public AccountController.AvailableColorsResponse getAvailableColors() {
        return new AccountController.AvailableColorsResponse(AVAILABLE_COLORS);
    }

    public Map<String, String> calculateColorsForTeams(Map<String, List<Player>> teams) {
        int n = teams.size();
        int m = AVAILABLE_COLORS.size();
        checkTeamsSize(n, m);

        List<String> teamNames = new ArrayList<>(teams.keySet());
        int[][] score = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                String color = AVAILABLE_COLORS.get(j);
                score[i][j] = (int) teams.get(teamNames.get(i))
                        .stream()
                        .filter(p -> color.equalsIgnoreCase(p.color()))
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
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < n; i++)
            if (assignment[i] >= 0)
                result.put(teamNames.get(i), AVAILABLE_COLORS.get(assignment[i]));
        return result;
    }

    private void checkTeamsSize(int teamSize, int colorsAvailable) {
        if (colorsAvailable < teamSize)
            throw new RuntimeException("There are more teams than available colors");
    }
}
