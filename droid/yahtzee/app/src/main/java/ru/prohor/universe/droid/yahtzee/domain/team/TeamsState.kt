package ru.prohor.universe.droid.yahtzee.domain.team

import androidx.compose.runtime.mutableStateListOf
import ru.prohor.universe.droid.yahtzee.domain.settings.SettingsState

object TeamsState {
    const val MAX_COUNT = 8

    private val teams = mutableStateListOf<Team>()

    fun shuffle() {
        val updatedTeams = if (SettingsState.settings.keepTeamsOrderOnShuffle) {
            shuffleOrdered()
        } else {
            shuffleUnordered()
        }

        teams.clear()
        teams.addAll(updatedTeams)
    }

    private fun shuffleOrdered(): List<Team> {
        if (count() < 2) return teams

        val shift = (1..(count() - 1)).random()
        return teams().drop(shift) + teams.take(shift)
    }

    private fun shuffleUnordered(): List<Team> {
        var shuffled: List<Team> = teams
        while (shuffled == teams) shuffled = teams.shuffled()
        return shuffled
    }

    fun isShuffleAvailable() = count() > 1

    fun count() = teams.count()

    fun isAdditionAvailable() = count() < MAX_COUNT

    fun removeAt(index: Int) {
        teams.removeAt(index)
    }

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

    fun moveTeam(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        val mutable = teams.toMutableList()
        val item = mutable.removeAt(fromIndex)
        mutable.add(toIndex, item)
        teams.clear()
        teams.addAll(mutable)
    }
}
