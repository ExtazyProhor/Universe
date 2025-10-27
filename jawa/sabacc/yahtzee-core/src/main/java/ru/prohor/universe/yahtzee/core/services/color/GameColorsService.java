package ru.prohor.universe.yahtzee.core.services.color;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.core.core.color.TeamColor;
import ru.prohor.universe.yahtzee.core.core.color.YahtzeeColor;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameColorsService {
    public GameColorsService(@Value("${universe.yahtzee.game.offline.max-teams}") int maxTeams) {
        checkTeamsSize(maxTeams);
    }

    public List<TeamColor> getAvailableColors() {
        return YahtzeeColor.asList();
    }

    public int getRandomColorId() {
        return YahtzeeColor.random.getColorId();
    }

    public boolean validateColor(int colorId) {
        return YahtzeeColor.getById(colorId) != null;
    }

    public Opt<TeamColor> getById(int colorId) {
        return Opt.ofNullable(YahtzeeColor.getActual(colorId)).map(YahtzeeColor::getTeamColor);
    }

    public Map<String, TeamColor> calculateColorsForTeams(Map<String, List<Player>> teams) {
        int n = teams.size();
        int m = YahtzeeColor.actualColors();
        checkTeamsSize(n);

        List<String> teamNames = new ArrayList<>(teams.keySet());
        int[][] score = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int colorId = YahtzeeColor.getById(j).getColorId();
                score[i][j] = (int) teams.get(teamNames.get(i))
                        .stream()
                        .filter(p -> colorId == YahtzeeColor.getById(p.color()).getColorId())
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
                result.put(teamNames.get(i), YahtzeeColor.getActual(assignment[i]).getTeamColor());
        return result;
    }

    private void checkTeamsSize(int teamSize) {
        if (YahtzeeColor.actualColors() < teamSize)
            throw new RuntimeException("There are more teams than available colors");
    }
}
