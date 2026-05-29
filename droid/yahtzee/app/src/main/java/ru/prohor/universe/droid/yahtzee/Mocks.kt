package ru.prohor.universe.droid.yahtzee

import ru.prohor.universe.droid.yahtzee.model.Team
import ru.prohor.universe.droid.yahtzee.state.TeamsState
import ru.prohor.universe.droid.yahtzee.ui.theme.TeamColors

object Mocks {
    fun init() {

    }

    fun oneTeam() {
        if (TeamsState.count() == 0) {
            TeamsState.save(Team("Alice", TeamColors.LIGHT_SEA_GREEN))
        }
    }

    fun threeTeams() {
        if (TeamsState.count() == 0) {
            TeamsState.save(Team("Alice", TeamColors.LIGHT_SEA_GREEN))
            TeamsState.save(Team("Bob", TeamColors.PURPLE))
            TeamsState.save(Team("Charlie", TeamColors.ORANGE_RED))
        }
    }

    fun addTeams() {
        if (TeamsState.count() == 0) {
            TeamsState.save(Team("Team-1", TeamColors.CRIMSON))
            TeamsState.save(Team("Team-2", TeamColors.GRAY))
            TeamsState.save(Team("Team-3", TeamColors.GOLD))
            TeamsState.save(Team("Team-4", TeamColors.BLACK))
            TeamsState.save(Team("Team-5", TeamColors.ROYAL_BLUE))
            TeamsState.save(Team("Team-6", TeamColors.LIGHT_SEA_GREEN))
            TeamsState.save(Team("Team-7", TeamColors.ORANGE_RED))
            TeamsState.save(Team("Team-8", TeamColors.LIME_GREEN))
        }
    }
}
