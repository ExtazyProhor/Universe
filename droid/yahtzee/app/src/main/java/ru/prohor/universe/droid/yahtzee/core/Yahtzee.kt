package ru.prohor.universe.droid.yahtzee.core

import ru.prohor.universe.droid.yahtzee.model.CombinationItem
import ru.prohor.universe.droid.yahtzee.model.ComplexCombination
import ru.prohor.universe.droid.yahtzee.model.MetaCombination
import ru.prohor.universe.droid.yahtzee.model.SimpleCombination

object Yahtzee {
    const val SCORE_TO_BONUS = 63
    const val BONUS_VALUE = 35

    fun recalculateMetaCombinations(combinations: MutableMap<CombinationItem, Int>) {
        val simple = combinations.filter { it.key is SimpleCombination }.map { it.value }.sum()
        val complex = combinations.filter { it.key is ComplexCombination }.map { it.value }.sum()

        val (bonus, scoreToBonus) = if (simple >= SCORE_TO_BONUS) {
            Pair(BONUS_VALUE, 0)
        } else {
            Pair(0, SCORE_TO_BONUS - simple)
        }
        val total = simple + complex + bonus

        combinations[MetaCombination.TOTAL] = total
        combinations[MetaCombination.SCORE_TO_BONUS] = scoreToBonus
    }
}
