package ru.prohor.universe.droid.yahtzee.mocks

import ru.prohor.universe.droid.yahtzee.domain.game.CHANCE
import ru.prohor.universe.droid.yahtzee.domain.game.Combination
import ru.prohor.universe.droid.yahtzee.domain.game.FixedValueCombination
import ru.prohor.universe.droid.yahtzee.domain.game.FreeValueCombination
import ru.prohor.universe.droid.yahtzee.domain.game.GameState
import ru.prohor.universe.droid.yahtzee.domain.game.SimpleCombination

object ScoresMocks {
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
}
