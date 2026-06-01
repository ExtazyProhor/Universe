package ru.prohor.universe.droid.yahtzee.mocks

import android.content.Context
import ru.prohor.universe.droid.yahtzee.model.ALL_COMBINATIONS
import ru.prohor.universe.droid.yahtzee.model.CHANCE
import ru.prohor.universe.droid.yahtzee.model.Combination
import ru.prohor.universe.droid.yahtzee.model.FixedValueCombination
import ru.prohor.universe.droid.yahtzee.model.FreeValueCombination
import ru.prohor.universe.droid.yahtzee.model.SavedCombination
import ru.prohor.universe.droid.yahtzee.model.SavedGame
import ru.prohor.universe.droid.yahtzee.model.SavedTeam
import ru.prohor.universe.droid.yahtzee.model.SimpleCombination
import ru.prohor.universe.droid.yahtzee.model.Team
import ru.prohor.universe.droid.yahtzee.state.GameState
import ru.prohor.universe.droid.yahtzee.state.SavedGamesState
import ru.prohor.universe.droid.yahtzee.state.TeamsState
import ru.prohor.universe.droid.yahtzee.ui.theme.TeamColors

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

    fun initScores() {

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
        TeamColors.CRIMSON,
        TeamColors.GRAY,
        TeamColors.GOLD,
        TeamColors.BLACK,
        TeamColors.ROYAL_BLUE,
        TeamColors.LIGHT_SEA_GREEN,
        TeamColors.ORANGE_RED,
        TeamColors.LIME_GREEN
    )
}
