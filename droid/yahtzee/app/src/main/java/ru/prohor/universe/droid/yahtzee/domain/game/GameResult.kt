package ru.prohor.universe.droid.yahtzee.domain.game

import ru.prohor.universe.droid.yahtzee.domain.team.Team

data class GameResult(
    val teams: List<TeamResult>
)

data class TeamResult(
    val total: Int,
    val team: Team
)
