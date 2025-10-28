package ru.prohor.universe.yahtzee.core.services.color;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.core.core.color.TeamColor;
import ru.prohor.universe.yahtzee.core.core.color.YahtzeeColor;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameColorsServiceTest {
    private static final Player mockPlayer = new Player(
            null, null, 0, null, 0, null, null, null, null, null, false, null, null
    );

    private GameColorsService service;

    //@BeforeEach
    void setUp() {
        service = new GameColorsService(10);
    }

    //@Test
    void testConstructorThrowsExceptionWhenMaxTeamsExceedsAvailableColors() {
        int tooManyTeams = 1000;
        //Assertions.assertThrows(RuntimeException.class, () -> new GameColorsService(tooManyTeams));
    }

    //@Test
    void testConstructorSucceedsWhenMaxTeamsIsValid() {
        //Assertions.assertDoesNotThrow(() -> new GameColorsService(5));
    }

    //@Test
    void testGetAvailableColorsReturnsNonEmptyList() {
        List<TeamColor> colors = service.getAvailableColors();
        //Assertions.assertNotNull(colors);
        //Assertions.assertFalse(colors.isEmpty());
    }

    //@Test
    void testGetRandomColorIdReturnsValidColorId() {
        int colorId = service.getRandomColorId();
        //Assertions.assertTrue(service.validateColor(colorId));
    }

    //@Test
    void testValidateColorReturnsTrueForValidColor() {
        int validColorId = YahtzeeColor.getById(0).getColorId();
        boolean result = service.validateColor(validColorId);
        //Assertions.assertTrue(result);
    }

    //@Test
    void testValidateColorReturnsFalseForInvalidColor() {
        int invalidColorId = -999;
        boolean result = service.validateColor(invalidColorId);
        //Assertions.assertFalse(result);
    }

    //@Test
    void testGetByIdReturnsOptionalWithColorWhenExists() {
        int validColorId = YahtzeeColor.getById(0).getColorId();
        Opt<TeamColor> result = service.getById(validColorId);
        //Assertions.assertTrue(result.isPresent());
    }

    //@Test
    void testGetByIdReturnsEmptyOptionalWhenColorDoesNotExist() {
        int invalidColorId = -999;
        Opt<TeamColor> result = service.getById(invalidColorId);
        //Assertions.assertFalse(result.isPresent());
    }

    //@Test
    void testCalculateColorsForTeamsThrowsExceptionWhenTooManyTeams() {
        Map<String, List<Player>> teams = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            teams.put("Team " + i, Collections.emptyList());
        }

        //Assertions.assertThrows(RuntimeException.class, () -> service.calculateColorsForTeams(teams));
    }

    //@Test
    void testCalculateColorsForTeamsReturnsCorrectMapping() {

        Player player1 = mockPlayer.toBuilder().color(YahtzeeColor.CRIMSON.getColorId()).build();
        Player player2 =  mockPlayer.toBuilder().color(YahtzeeColor.ORANGE_RED.getColorId()).build();
        Player player3 =  mockPlayer.toBuilder().color(YahtzeeColor.CRIMSON.getColorId()).build();

        Map<String, List<Player>> teams = new HashMap<>();
        teams.put("Team A", List.of(player1, player3));
        teams.put("Team B", List.of(player2));

        Map<String, TeamColor> result = service.calculateColorsForTeams(teams);

        //Assertions.assertNotNull(result);
        //Assertions.assertEquals(2, result.size());
        //Assertions.assertTrue(result.containsKey("Team A"));
        //Assertions.assertTrue(result.containsKey("Team B"));
    }

    //@Test
    void testCalculateColorsForTeamsOptimizesColorAssignment1() {
        int color1 = YahtzeeColor.CRIMSON.getColorId();
        int color2 = YahtzeeColor.ORANGE_RED.getColorId();

        Player player1 = mockPlayer.toBuilder().color(color1).build();
        Player player2 =  mockPlayer.toBuilder().color(color1).build();
        Player player3 =  mockPlayer.toBuilder().color(color2).build();

        Map<String, List<Player>> teams = new HashMap<>();
        teams.put("Team A", List.of(player1, player2));
        teams.put("Team B", List.of(player3));

        Map<String, TeamColor> result = service.calculateColorsForTeams(teams);

        //Assertions.assertNotNull(result);
        //Assertions.assertEquals(color1, result.get("Team A").colorId());
        //Assertions.assertEquals(color2, result.get("Team B").colorId());
    }

    //@Test
    void testCalculateColorsForTeamsOptimizesColorAssignment2() {
        Map<String, List<Player>> teams = new HashMap<>();
        teams.put("Team A", List.of(
                mockPlayer.toBuilder().color(YahtzeeColor.CRIMSON.getColorId()).build(),
                mockPlayer.toBuilder().color(YahtzeeColor.CRIMSON.getColorId()).build()
        ));
        teams.put("Team B", List.of(
                mockPlayer.toBuilder().color(YahtzeeColor.random.getColorId()).build(),
                mockPlayer.toBuilder().color(YahtzeeColor.ORANGE_RED.getColorId()).build()
        ));
        teams.put("Team C", List.of(
                mockPlayer.toBuilder().color(YahtzeeColor.BLACK.getColorId()).build(),
                mockPlayer.toBuilder().color(YahtzeeColor.BLACK.getColorId()).build()
        ));

        Map<String, TeamColor> result = service.calculateColorsForTeams(teams);

        //Assertions.assertNotNull(result);
        //Assertions.assertEquals(YahtzeeColor.CRIMSON.getTeamColor(), result.get("Team A").colorId());
        //Assertions.assertNotEquals(YahtzeeColor.CRIMSON.getTeamColor(), result.get("Team B").colorId());
        //Assertions.assertNotEquals(YahtzeeColor.BLACK.getTeamColor(), result.get("Team B").colorId());
        //Assertions.assertEquals(YahtzeeColor.BLACK.getTeamColor(), result.get("Team C").colorId());
    }

    //@Test
    void testCalculateColorsForTeamsOptimizesColorAssignment3() {
        int color1 = YahtzeeColor.CRIMSON.getColorId();
        int color2 = YahtzeeColor.ORANGE_RED.getColorId();
        int color3 = YahtzeeColor.FOREST_GREEN.getColorId();
        int color4 = YahtzeeColor.ROYAL_BLUE.getColorId();

        Map<String, List<Player>> teams = new HashMap<>();
        teams.put("Team A", List.of(
                mockPlayer.toBuilder().color(color1).build(),
                mockPlayer.toBuilder().color(color2).build()
        ));
        teams.put("Team B", List.of(
                mockPlayer.toBuilder().color(color3).build(),
                mockPlayer.toBuilder().color(color4).build()
        ));

        Map<String, TeamColor> result = service.calculateColorsForTeams(teams);

        //Assertions.assertNotNull(result);
        //Assertions.assertTrue(List.of(color1, color2).contains(result.get("Team A").colorId()));
        //Assertions.assertTrue(List.of(color3, color4).contains(result.get("Team B").colorId()));
    }

    //@Test
    void testCalculateColorsForTeamsOptimizesColorAssignment4() {
        int color1 = YahtzeeColor.CRIMSON.getColorId();
        int color2 = YahtzeeColor.ORANGE_RED.getColorId();
        int color3 = YahtzeeColor.FOREST_GREEN.getColorId();

        Map<String, List<Player>> teams = new HashMap<>();
        teams.put("Team A", List.of(
                mockPlayer.toBuilder().color(color1).build(),
                mockPlayer.toBuilder().color(color2).build()
        ));
        teams.put("Team B", List.of(
                mockPlayer.toBuilder().color(color2).build(),
                mockPlayer.toBuilder().color(color3).build()
        ));

        Map<String, TeamColor> result = service.calculateColorsForTeams(teams);

        //Assertions.assertNotNull(result);
        //Assertions.assertNotEquals(result.get("Team A").colorId(), result.get("Team B").colorId());
        //Assertions.assertTrue(List.of(color1, color2).contains(result.get("Team A").colorId()));
        //Assertions.assertTrue(List.of(color2, color3).contains(result.get("Team B").colorId()));
    }

    //@Test
    void testCalculateColorsForTeamsRandomColorAssignment() {
        Map<String, List<Player>> teams = new HashMap<>();
        teams.put("Team A", List.of(
                mockPlayer.toBuilder().color(YahtzeeColor.random.getColorId()).build(),
                mockPlayer.toBuilder().color(YahtzeeColor.random.getColorId()).build()
        ));
        teams.put("Team B", List.of(
                mockPlayer.toBuilder().color(YahtzeeColor.random.getColorId()).build(),
                mockPlayer.toBuilder().color(YahtzeeColor.random.getColorId()).build()
        ));
        teams.put("Team C", List.of(
                mockPlayer.toBuilder().color(YahtzeeColor.random.getColorId()).build(),
                mockPlayer.toBuilder().color(YahtzeeColor.random.getColorId()).build()
        ));

        Map<String, TeamColor> result = service.calculateColorsForTeams(teams);
        //Assertions.assertNotNull(result);

        Set<Integer> set = result.values().stream().map(TeamColor::colorId).collect(Collectors.toSet());
        //Assertions.assertEquals(3, set.size());
    }

    //@Test
    void testCalculateColorsForSingleTeam() {
        Player player = mockPlayer.toBuilder().color(YahtzeeColor.BLACK.getColorId()).build();
        Map<String, List<Player>> teams = new HashMap<>();
        teams.put("Team Solo", List.of(player));

        Map<String, TeamColor> result = service.calculateColorsForTeams(teams);

        //Assertions.assertNotNull(result);
        //Assertions.assertEquals(1, result.size());
        //Assertions.assertTrue(result.containsKey("Team Solo"));
        //Assertions.assertEquals(YahtzeeColor.BLACK.getTeamColor(), result.get("Team Solo"));
    }

    //@Test
    void testCalculateColorsForTeamsWithNoMatchingPlayerColors() {
        int color1 = YahtzeeColor.CRIMSON.getColorId();
        int color2 = YahtzeeColor.FOREST_GREEN.getColorId();

        Player player1 = mockPlayer.toBuilder().color(color1).build();
        Player player2 = mockPlayer.toBuilder().color(color2).build();

        Map<String, List<Player>> teams = new HashMap<>();
        teams.put("Team A", Collections.singletonList(player1));
        teams.put("Team B", Collections.singletonList(player2));

        Map<String, TeamColor> result = service.calculateColorsForTeams(teams);

        //Assertions.assertNotNull(result);
        //Assertions.assertEquals(2, result.size());
    }
}
