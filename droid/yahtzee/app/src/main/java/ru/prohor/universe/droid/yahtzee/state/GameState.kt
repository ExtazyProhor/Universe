package ru.prohor.universe.droid.yahtzee.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import ru.prohor.universe.droid.yahtzee.core.Yahtzee
import ru.prohor.universe.droid.yahtzee.model.Combination
import ru.prohor.universe.droid.yahtzee.model.CombinationItem
import ru.prohor.universe.droid.yahtzee.model.MetaCombination
import ru.prohor.universe.droid.yahtzee.model.Team

object GameState {
    private val scores = mutableStateMapOf<Team, SnapshotStateMap<CombinationItem, Int>>()
    private var currentTeamIndex by mutableIntStateOf(0)
    private var lastCombination by mutableStateOf<Combination?>(null)

    fun initialize() {
        TeamsState.teams().forEach { team ->
            if (team in scores) return@forEach
            scores[team] = mutableStateMapOf(
                MetaCombination.TOTAL to 0,
                MetaCombination.SCORE_TO_BONUS to Yahtzee.SCORE_TO_BONUS,
            )
        }
    }

    fun score(team: Team, combination: CombinationItem): Int? {
        return scores[team]?.get(combination)
    }

    fun currentTeam(): Team {
        return TeamsState.teams()[currentTeamIndex]
    }

    fun previousTeam(): Team {
        return TeamsState.team(previousTurn())
    }

    fun nextTeam(): Team {
        return TeamsState.team(nextTurn())
    }

    fun setScore(combination: Combination, value: Int) {
        val team = currentTeam()
        lastCombination = combination
        scores[team]?.set(combination, value)
        currentTeamIndex = nextTurn()
        scores[team]?.let { Yahtzee.recalculateMetaCombinations(it) }
    }

    fun undoLastMove() {
        val combination = lastCombination ?: return
        currentTeamIndex = previousTurn()
        val team = TeamsState.team(currentTeamIndex)
        scores[team]?.remove(combination)
        lastCombination = null
        scores[team]?.let { Yahtzee.recalculateMetaCombinations(it) }
    }

    fun isUndoAvailable(): Boolean {
        return lastCombination != null
    }

    private fun nextTurn(): Int {
        var next = currentTeamIndex + 1
        if (next >= TeamsState.count()) next = 0
        return next
    }

    private fun previousTurn(): Int {
        var next = currentTeamIndex - 1
        if (next < 0) next = TeamsState.count() - 1
        return next
    }
}
