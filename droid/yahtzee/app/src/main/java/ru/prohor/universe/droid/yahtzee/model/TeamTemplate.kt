package ru.prohor.universe.droid.yahtzee.model

import kotlinx.serialization.Serializable
import ru.prohor.universe.droid.yahtzee.ui.theme.TeamColor

@Serializable
data class TeamTemplate(
    val name: String,
    val color: TeamColor,
    val usages: Int
)
