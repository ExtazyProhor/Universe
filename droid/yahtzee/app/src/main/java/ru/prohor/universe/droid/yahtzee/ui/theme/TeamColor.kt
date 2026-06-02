package ru.prohor.universe.droid.yahtzee.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

enum class TeamColor(
    mainColor: String,
    textColor: String,
    lightColor: String,
    darkColor: String
) {
    CRIMSON("DC143C", "FFFFFF", "FFE6E6", "8B0000"),
    ORANGE_RED("FF4500", "FFFFFF", "FFE4E1", "CC3700"),
    DARK_ORANGE("FF8C00", "FFFFFF", "FFE4B5", "CC7000"),
    GOLD("FFD700", "000000", "FFF8DC", "B8860B"),
    LIME_GREEN("32CD32", "FFFFFF", "F0FFF0", "228B22"),
    FOREST_GREEN("2E7D32", "FFFFFF", "E8F5E9", "1B5E20"),
    LIGHT_SEA_GREEN("20B2AA", "FFFFFF", "E0FFFF", "008B8B"),
    DEEP_SKY_BLUE("00BFFF", "FFFFFF", "F0F8FF", "0080CC"),
    ROYAL_BLUE("1E70FF", "FFFFFF", "E6F2FF", "104E8B"),
    MEDIUM_PURPLE("9370DB", "FFFFFF", "E6E6FA", "663399"),
    PURPLE("7B1FA2", "FFFFFF", "F3E5F5", "4A148C"),
    HOT_PINK("FF69B4", "FFFFFF", "FFE4E1", "CC5490"),
    TAN("D2B48C", "000000", "FAEBD7", "A68B65"),
    GRAY("808080", "FFFFFF", "DCDCDC", "404040"),
    SADDLE_BROWN("8B4513", "FFFFFF", "F5DEB3", "5C2E0D"),
    BLACK("000000", "FFFFFF", "CCCCCC", "000000");

    val mainColor = mainColor.toColor()
    val textColor = textColor.toColor()
    val lightColor = lightColor.toColor()
    val darkColor = darkColor.toColor()

    private fun String.toColor(): Color {
        return Color(("#$this").toColorInt())
    }
}
