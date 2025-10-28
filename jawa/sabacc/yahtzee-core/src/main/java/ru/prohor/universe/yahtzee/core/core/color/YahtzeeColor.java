package ru.prohor.universe.yahtzee.core.core.color;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
public enum YahtzeeColor implements SelectedColor {
    CRIMSON(new TeamColor(0, "DC143C", "FFFFFF", "FFE6E6", "8B0000")),
    ORANGE_RED(new TeamColor(1, "FF4500", "FFFFFF", "FFE4E1", "CC3700")),
    DARK_ORANGE(new TeamColor(2, "FF8C00", "FFFFFF", "FFE4B5", "CC7000")),
    GOLD(new TeamColor(3, "FFD700", "000000", "FFF8DC", "B8860B")),
    LIME_GREEN(new TeamColor(4, "32CD32", "FFFFFF", "F0FFF0", "228B22")),
    FOREST_GREEN(new TeamColor(5, "2E7D32", "FFFFFF", "E8F5E9", "1B5E20")),
    LIGHT_SEA_GREEN(new TeamColor(6, "20B2AA", "FFFFFF", "E0FFFF", "008B8B")),
    DEEP_SKY_BLUE(new TeamColor(7, "00BFFF", "FFFFFF", "F0F8FF", "0080CC")),
    ROYAL_BLUE(new TeamColor(8, "1E70FF", "FFFFFF", "E6F2FF", "104E8B")),
    MEDIUM_PURPLE(new TeamColor(9, "9370DB", "FFFFFF", "E6E6FA", "663399")),
    PURPLE(new TeamColor(10, "7B1FA2", "FFFFFF", "F3E5F5", "4A148C")),
    HOT_PINK(new TeamColor(11, "FF69B4", "FFFFFF", "FFE4E1", "CC5490")),
    TAN(new TeamColor(12, "D2B48C", "000000", "FAEBD7", "A68B65")),
    GRAY(new TeamColor(13, "808080", "FFFFFF", "DCDCDC", "404040")),
    SADDLE_BROWN(new TeamColor(14, "8B4513", "FFFFFF", "F5DEB3", "5C2E0D")),
    BLACK(new TeamColor(15, "000000", "FFFFFF", "CCCCCC", "000000"));

    private static final List<TeamColor> TEAM_COLOR_LIST = Arrays.stream(YahtzeeColor.values())
            .map(YahtzeeColor::getTeamColor)
            .toList();

    private static final Map<Integer, YahtzeeColor> MAP = Arrays.stream(values()).collect(Collectors.toMap(
            YahtzeeColor::getColorId,
            MonoFunction.identity()
    ));

    public static final SelectedColor random = new RandomColor(values());

    private final TeamColor color;

    YahtzeeColor(TeamColor color) {
        this.color = color;
    }

    public int getColorId() {
        return color.colorId();
    }

    public TeamColor getTeamColor() {
        return color;
    }

    public static SelectedColor getById(int id) {
        return id == -1 ? random : MAP.get(id);
    }

    public static YahtzeeColor getActual(int id) {
        return MAP.get(id);
    }

    public static List<TeamColor> asList() {
        return TEAM_COLOR_LIST;
    }

    public static int actualColors() {
        return values().length;
    }
}
