package ru.prohor.universe.droid.yahtzee.domain.game

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedCombination
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedGame
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedGamesState
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedTeam
import ru.prohor.universe.droid.yahtzee.domain.team.Team
import ru.prohor.universe.droid.yahtzee.domain.team.TeamsState

object GameState {
    private val scores = mutableStateMapOf<Team, SnapshotStateMap<CombinationItem, Int>>()
    private lateinit var params: GameStateParams

    fun initialize() {
        scores.forEach { it.value.clear() }
        TeamsState.teams().forEach { team ->
            scores[team] = mutableStateMapOf(
                MetaCombination.TOTAL to 0,
                MetaCombination.SCORE_TO_BONUS to Yahtzee.SCORE_TO_BONUS,
            )
        }
        params = GameStateParams()
    }

    fun isGameStarted(): Boolean {
        return scores.values.all { it.size == 2 }
    }

    fun score(team: Team, combination: CombinationItem): Int? {
        return scores[team]?.get(combination)
    }

    fun currentTeam(): Team {
        return TeamsState.team(params.currentTeamIndex)
    }

    fun previousTeam(): Team {
        return TeamsState.team(previousTurn())
    }

    fun nextTeam(): Team {
        return TeamsState.team(nextTurn())
    }

    fun setScore(combination: Combination, value: Int) {
        val team = currentTeam()
        params.lastCombination = combination
        scores[team]?.set(combination, value)
        params.currentTeamIndex = nextTurn()
        scores[team]?.let { Yahtzee.recalculateMetaCombinations(it) }
        params.gameFinished = calculateIsGameFinished()
    }

    private fun calculateIsGameFinished(): Boolean {
        val combinations = scores.map { it.value.size }.sum()
        return combinations == TeamsState.count() * Yahtzee.COMBINATIONS_WITH_META_COUNT
    }

    fun isGameFinished() = params.gameFinished

    fun getResults(): GameResult {
        return GameResult(
            scores.map {
                TeamResult(
                    total = it.value[MetaCombination.TOTAL] ?: 0,
                    team = it.key
                )
            }.sortedByDescending { it.total }
        )
    }

    fun saveGame(context: Context) {
        if (params.saved) return
        params.saved = true

        val game = SavedGame(
            teams = scores.map { team ->
                SavedTeam(
                    name = team.key.name,
                    scores = team.value.map { score ->
                        SavedCombination(
                            combination = score.key.toString(),
                            value = score.value
                        )
                    }
                )
            }
        )
        SavedGamesState.save(game, context)
    }

    fun undoLastMove() {
        val combination = params.lastCombination ?: return
        params.currentTeamIndex = previousTurn()
        val team = TeamsState.team(params.currentTeamIndex)
        scores[team]?.remove(combination)
        params.lastCombination = null
        scores[team]?.let { Yahtzee.recalculateMetaCombinations(it) }
        params.gameFinished = calculateIsGameFinished()
    }

    fun isUndoAvailable(): Boolean {
        return params.lastCombination != null
    }

    private fun nextTurn(): Int {
        var next = params.currentTeamIndex + 1
        if (next >= TeamsState.count()) next = 0
        return next
    }

    private fun previousTurn(): Int {
        var next = params.currentTeamIndex - 1
        if (next < 0) next = TeamsState.count() - 1
        return next
    }
}
