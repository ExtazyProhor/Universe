package ru.prohor.universe.droid.yahtzee.mocks

import ru.prohor.universe.droid.yahtzee.model.CHANCE
import ru.prohor.universe.droid.yahtzee.model.Combination
import ru.prohor.universe.droid.yahtzee.model.ComplexCombination
import ru.prohor.universe.droid.yahtzee.model.FixedValueCombination
import ru.prohor.universe.droid.yahtzee.model.FreeValueCombination
import ru.prohor.universe.droid.yahtzee.model.SimpleCombination

object CombinationsMocks {
    fun generateFor(combination: Combination): Int {
        return when (combination) {
            is SimpleCombination -> when (combination) {
                SimpleCombination.UNITS -> randomSimple(1)
                SimpleCombination.TWOS -> randomSimple(2)
                SimpleCombination.THREES -> randomSimple(3)
                SimpleCombination.FOURS -> randomSimple(4)
                SimpleCombination.FIVES -> randomSimple(5)
                SimpleCombination.SIXES -> randomSimple(6)
            }

            is ComplexCombination -> when (combination) {
                is FreeValueCombination -> when (combination) {
                    FreeValueCombination.PAIR -> randomComplex()
                    FreeValueCombination.TWO_PAIRS -> randomComplex()
                    FreeValueCombination.THREE_OF_KIND -> randomComplex()
                    FreeValueCombination.FOUR_OF_KIND -> randomComplex()
                }

                is FixedValueCombination -> when (combination) {
                    FixedValueCombination.FULL_HOUSE -> randomFixedComplex(25)
                    FixedValueCombination.LOW_STRAIGHT -> randomFixedComplex(30)
                    FixedValueCombination.HIGH_STRAIGHT -> randomFixedComplex(40)
                    FixedValueCombination.YAHTZEE -> randomFixedComplex(50)
                }

                is CHANCE -> randomComplex()
            }
        }
    }

    private fun randomSimple(base: Int): Int = (1..5).random() * base

    private fun randomComplex() = (5..30).random()

    private fun randomFixedComplex(value: Int) = listOf(0, value).random()
}
