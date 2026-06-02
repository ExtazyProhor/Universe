package ru.prohor.universe.droid.yahtzee.mocks

import android.content.Context
import ru.prohor.universe.droid.yahtzee.domain.game.ALL_COMBINATIONS
import ru.prohor.universe.droid.yahtzee.domain.game.CHANCE
import ru.prohor.universe.droid.yahtzee.domain.game.Combination
import ru.prohor.universe.droid.yahtzee.domain.game.FixedValueCombination
import ru.prohor.universe.droid.yahtzee.domain.game.FreeValueCombination
import ru.prohor.universe.droid.yahtzee.domain.game.GameState
import ru.prohor.universe.droid.yahtzee.domain.game.SimpleCombination
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedCombination
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedGame
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedGamesState
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedTeam
import ru.prohor.universe.droid.yahtzee.domain.team.Team
import ru.prohor.universe.droid.yahtzee.domain.team.TeamColor
import ru.prohor.universe.droid.yahtzee.domain.team.TeamsState

object Mocks {
    fun initGames(context: Context) {

    }

    private fun games(count: Int, context: Context) {
        repeat(count) {
            game((1..6).random(), context)
        }
    }

    private fun game(teams: Int, context: Context) {
        val game = SavedGame(
            teams = List(teams) { index ->
                SavedTeam(
                    name = NAMES[index],
                    scores = ALL_COMBINATIONS.map {
                        SavedCombination(
                            combination = it.name,
                            value = CombinationsMocks.generateFor(it)
                        )
                    }
                )
            }
        )
        SavedGamesState.save(game, context)
    }

    fun initTeams() {

    }

    private fun teams(count: Int) {
        for (i in 0 until count) {
            TeamsState.save(Team(NAMES[i], COLORS[i]))
        }
    }

    private var scoresInitiated = false

    fun initScores() {
        if (scoresInitiated) return
        scoresInitiated = true

    }

    fun simpleScores(count: Int) {
        SimpleCombination.entries.forEach {
            combination(it, count)
        }
    }

    fun almostAllScores(count: Int) {
        simpleScores(count)
        FreeValueCombination.entries.forEach { combination(it, count) }
        FixedValueCombination.entries.forEach { combination(it, count) }
    }

    fun allScores(count: Int) {
        almostAllScores(count)
        combination(CHANCE, count)
    }

    private fun combination(combination: Combination, repeat: Int) {
        repeat(repeat) {
            GameState.setScore(
                combination,
                CombinationsMocks.generateFor(combination)
            )
        }
    }

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
