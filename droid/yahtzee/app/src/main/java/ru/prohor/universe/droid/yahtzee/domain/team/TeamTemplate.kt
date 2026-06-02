package ru.prohor.universe.droid.yahtzee.domain.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamTemplate(
    val name: String,
    val color: TeamColor,
    val usages: Int
)
