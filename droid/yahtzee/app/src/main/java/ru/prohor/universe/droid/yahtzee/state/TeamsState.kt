package ru.prohor.universe.droid.yahtzee.state

import androidx.compose.runtime.mutableStateListOf
import ru.prohor.universe.droid.yahtzee.model.IndexedTeam
import ru.prohor.universe.droid.yahtzee.model.Team
import ru.prohor.universe.droid.yahtzee.ui.theme.TeamColor

object TeamsState {
    private const val MAX_COUNT = 8

    private val teams = mutableStateListOf<Team>()

    fun shuffle() {
        val shuffled = teams.shuffled()
        teams.clear()
        teams.addAll(shuffled)
    }

    fun isShuffleAvailable() = count() > 1

    fun count() = teams.count()

    fun isAdditionAvailable() = count() < MAX_COUNT

    fun removeAt(index: Int) {
        teams.removeAt(index)
    }

    fun moveTeamUp(index: Int) {
        switchTeams(index - 1)
    }

    fun moveTeamDown(index: Int) {
        switchTeams(index)
    }

    fun isLastIndex(index: Int) = index == teams.lastIndex

    fun getAllIndexed(): List<IndexedTeam> {
        return teams.mapIndexed { index, team -> IndexedTeam(index, team) }
    }

    fun teams(): List<Team> = teams

    fun team(index: Int): Team = teams[index]

    private fun addTeam(team: Team) {
        teams.add(team)
    }

    private fun changeTeam(index: Int, team: Team) {
        teams[index] = team
    }

    fun save(team: Team, editingTeamIndex: Int? = null) {
        if (editingTeamIndex == null) {
            addTeam(team)
        } else {
            changeTeam(editingTeamIndex, team)
        }
    }

    fun isAvailableToStartGame() = count() > 0

    fun usedNames(): Set<String> {
        return teams.map { it.name }.toSet()
    }

    fun usedColors(): MutableSet<TeamColor> {
        return teams.map { it.color }.toMutableSet()
    }

    private fun switchTeams(topIndex: Int) {
        if (topIndex < 0 || topIndex >= teams.lastIndex) return

        val bottomIndex = topIndex + 1
        val mutable = teams.toMutableList()

        val topTeam = mutable[topIndex]
        mutable[topIndex] = mutable[bottomIndex]
        mutable[bottomIndex] = topTeam

        teams.clear()
        teams.addAll(mutable)
    }
}
