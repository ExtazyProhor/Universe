package ru.prohor.universe.droid.yahtzee.model

import ru.prohor.universe.droid.yahtzee.ui.theme.TeamColor

const val MAX_TEAM_NAME_LENGTH = 20

data class Team(
    val name: String,
    val color: TeamColor
)

data class IndexedTeam(
    val index: Int,
    val team: Team
)
