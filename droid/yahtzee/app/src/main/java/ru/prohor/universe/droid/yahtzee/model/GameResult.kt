package ru.prohor.universe.droid.yahtzee.model

data class GameResult(
    val teams: List<TeamResult>
)

data class TeamResult(
    val total: Int,
    val team: Team
)
