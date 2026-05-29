package ru.prohor.universe.droid.yahtzee

import ru.prohor.universe.droid.yahtzee.model.Combination
import ru.prohor.universe.droid.yahtzee.model.ComplexCombination
import ru.prohor.universe.droid.yahtzee.model.SimpleCombination
import ru.prohor.universe.droid.yahtzee.model.Team
import ru.prohor.universe.droid.yahtzee.state.GameState
import ru.prohor.universe.droid.yahtzee.state.TeamsState
import ru.prohor.universe.droid.yahtzee.ui.theme.TeamColors

object Mocks {
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
        combination(SimpleCombination.UNITS, randomSimple(1), count)
        combination(SimpleCombination.TWOS, randomSimple(2), count)
        combination(SimpleCombination.THREES, randomSimple(3), count)
        combination(SimpleCombination.FOURS, randomSimple(4), count)
        combination(SimpleCombination.FIVES, randomSimple(5), count)
        combination(SimpleCombination.SIXES, randomSimple(6), count)
    }

    fun almostAllScores(count: Int) {
        simpleScores(count)

        combination(ComplexCombination.PAIR, randomComplex(), count)
        combination(ComplexCombination.TWO_PAIRS, randomComplex(), count)
        combination(ComplexCombination.THREE_OF_KIND, randomComplex(), count)
        combination(ComplexCombination.FOUR_OF_KIND, randomComplex(), count)
        combination(ComplexCombination.FULL_HOUSE, randomFixedComplex(25), count)
        combination(ComplexCombination.LOW_STRAIGHT, randomFixedComplex(30), count)
        combination(ComplexCombination.HIGH_STRAIGHT, randomFixedComplex(40), count)
        combination(ComplexCombination.YAHTZEE, randomFixedComplex(50), count)
    }

    fun allScores(count: Int) {
        almostAllScores(count)
        combination(ComplexCombination.CHANCE, randomComplex(), count)
    }

    private fun combination(combination: Combination, value: Int, repeat: Int) {
        repeat(repeat) { GameState.setScore(combination, value) }
    }

    private fun randomSimple(base: Int): Int = (0..5).random() * base

    private fun randomComplex() = (5..30).random()

    private fun randomFixedComplex(value: Int) = listOf(0, value).random()

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
