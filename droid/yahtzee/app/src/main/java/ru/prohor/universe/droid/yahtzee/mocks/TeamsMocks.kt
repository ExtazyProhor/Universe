package ru.prohor.universe.droid.yahtzee.mocks

import ru.prohor.universe.droid.yahtzee.domain.team.Team
import ru.prohor.universe.droid.yahtzee.domain.team.TeamColor
import ru.prohor.universe.droid.yahtzee.domain.team.TeamsState

object TeamsMocks {
    fun generateTeams(count: Int) {
        for (i in 0 until count) {
            TeamsState.save(Team(NAMES[i], COLORS[i]))
        }
    }

    fun generateTeamName(i: Int) = NAMES[i]

    private val NAMES = listOf(
        "Alice",
        "Bob",
        "Charlie Long Name",
        "David",
        "Eve",
        "Frank",
        "George",
        "Hi WWWWWWWWWWWWWWWWY"
    )

    private val COLORS = listOf(
        TeamColor.CRIMSON,
        TeamColor.GRAY,
        TeamColor.GOLD,
        TeamColor.BLACK,
        TeamColor.ROYAL_BLUE,
        TeamColor.LIGHT_SEA_GREEN,
        TeamColor.ORANGE_RED,
        TeamColor.LIME_GREEN
    )
}
