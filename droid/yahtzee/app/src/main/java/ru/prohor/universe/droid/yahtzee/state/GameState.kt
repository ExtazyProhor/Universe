package ru.prohor.universe.droid.yahtzee.state

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import ru.prohor.universe.droid.yahtzee.core.Yahtzee
import ru.prohor.universe.droid.yahtzee.model.Combination
import ru.prohor.universe.droid.yahtzee.model.CombinationItem
import ru.prohor.universe.droid.yahtzee.model.GameResult
import ru.prohor.universe.droid.yahtzee.model.MetaCombination
import ru.prohor.universe.droid.yahtzee.model.SavedCombination
import ru.prohor.universe.droid.yahtzee.model.SavedGame
import ru.prohor.universe.droid.yahtzee.model.SavedTeam
import ru.prohor.universe.droid.yahtzee.model.Team
import ru.prohor.universe.droid.yahtzee.model.TeamResult
import java.time.Clock
import java.time.Instant
import java.util.UUID

object GameState {
    private val scores = mutableStateMapOf<Team, SnapshotStateMap<CombinationItem, Int>>()
    private var currentTeamIndex by mutableIntStateOf(0)
    private var lastCombination by mutableStateOf<Combination?>(null)
    private var gameFinished by mutableStateOf(false)
    private var saved = false

    fun initialize() {
        scores.forEach { it.value.clear() }
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
        return TeamsState.team(currentTeamIndex)
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
        gameFinished = calculateIsGameFinished()
    }

    private fun calculateIsGameFinished(): Boolean {
        val combinations = scores.map { it.value.size }.sum()
        return combinations == TeamsState.count() * Yahtzee.COMBINATIONS_WITH_META_COUNT
    }

    fun isGameFinished() = gameFinished

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
        if (saved) return
        saved = true

        val game = SavedGame(
            uuid = UUID.randomUUID().toString(),
            finish = Instant.now(Clock.systemUTC()).epochSecond,
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
        val combination = lastCombination ?: return
        currentTeamIndex = previousTurn()
        val team = TeamsState.team(currentTeamIndex)
        scores[team]?.remove(combination)
        lastCombination = null
        scores[team]?.let { Yahtzee.recalculateMetaCombinations(it) }
        gameFinished = calculateIsGameFinished()
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
