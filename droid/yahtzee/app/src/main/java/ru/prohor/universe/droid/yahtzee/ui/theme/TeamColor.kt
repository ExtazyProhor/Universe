package ru.prohor.universe.droid.yahtzee.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

class TeamColor(
    mainColor: String,
    textColor: String,
    lightColor: String,
    darkColor: String
) {
    val mainColor = mainColor.toColor()
    val textColor = textColor.toColor()
    val lightColor = lightColor.toColor()
    val darkColor = darkColor.toColor()

    private fun String.toColor(): Color {
        return Color(("#$this").toColorInt())
    }
}

object TeamColors {
    val CRIMSON = TeamColor("DC143C", "FFFFFF", "FFE6E6", "8B0000")
    val ORANGE_RED = TeamColor("FF4500", "FFFFFF", "FFE4E1", "CC3700")
    val DARK_ORANGE = TeamColor("FF8C00", "FFFFFF", "FFE4B5", "CC7000")
    val GOLD = TeamColor("FFD700", "000000", "FFF8DC", "B8860B")
    val LIME_GREEN = TeamColor("32CD32", "FFFFFF", "F0FFF0", "228B22")
    val FOREST_GREEN = TeamColor("2E7D32", "FFFFFF", "E8F5E9", "1B5E20")
    val LIGHT_SEA_GREEN = TeamColor("20B2AA", "FFFFFF", "E0FFFF", "008B8B")
    val DEEP_SKY_BLUE = TeamColor("00BFFF", "FFFFFF", "F0F8FF", "0080CC")
    val ROYAL_BLUE = TeamColor("1E70FF", "FFFFFF", "E6F2FF", "104E8B")
    val MEDIUM_PURPLE = TeamColor("9370DB", "FFFFFF", "E6E6FA", "663399")
    val PURPLE = TeamColor("7B1FA2", "FFFFFF", "F3E5F5", "4A148C")
    val HOT_PINK = TeamColor("FF69B4", "FFFFFF", "FFE4E1", "CC5490")
    val TAN = TeamColor("D2B48C", "000000", "FAEBD7", "A68B65")
    val GRAY = TeamColor("808080", "FFFFFF", "DCDCDC", "404040")
    val SADDLE_BROWN = TeamColor("8B4513", "FFFFFF", "F5DEB3", "5C2E0D")
    val BLACK = TeamColor("000000", "FFFFFF", "CCCCCC", "000000")
}

val teamColors = listOf(
    TeamColors.CRIMSON,
    TeamColors.ORANGE_RED,
    TeamColors.DARK_ORANGE,
    TeamColors.GOLD,
    TeamColors.LIME_GREEN,
    TeamColors.FOREST_GREEN,
    TeamColors.LIGHT_SEA_GREEN,
    TeamColors.DEEP_SKY_BLUE,
    TeamColors.ROYAL_BLUE,
    TeamColors.MEDIUM_PURPLE,
    TeamColors.PURPLE,
    TeamColors.HOT_PINK,
    TeamColors.TAN,
    TeamColors.GRAY,
    TeamColors.SADDLE_BROWN,
    TeamColors.BLACK
)
