package ru.prohor.universe.kt.padawan.scripts

import kotlin.math.pow

fun main() {
    println(ShortStreetSolver.calculate())
    println(LongStreetSolver.calculate())
    println(FullHouseSolver.calculate())
    println(YahtzeeSolver.calculate())
}

object ShortStreetSolver : ComplexCombinationSolver() {
    override fun List<Int>.isValid(): Boolean {
        val set = toHashSet()
        if (!set.containsAll(setOf(3, 4))) {
            return false
        }
        return set.containsAll(setOf(1, 2)) || set.containsAll(setOf(2, 5)) || set.containsAll(setOf(5, 6))
    }

    private val lists = listOf(
        listOf(1, 2),
        listOf(2, 5),
        listOf(5, 6)
    )

    private val units = listOf(2, 5)

    override fun List<Int>.holdDiceFor(): List<Int> {
        val set = toMutableSet()
        set.removeAll { it in 3..4 }
        val result = mutableListOf<Int>()
        for (i in 3..4) {
            if (contains(i)) {
                result.add(i)
            }
        }

        if (set.isEmpty()) {
            return result
        }

        for (list in lists) {
            if (set.containsAll(list)) {
                result.addAll(list)
                return result
            }
        }

        for (unit in units) {
            if (set.contains(unit)) {
                result.add(unit)
                return result
            }
        }

        result.add(set.first())
        return result
    }
}

object LongStreetSolver : ComplexCombinationSolver() {
    override fun List<Int>.isValid(): Boolean {
        val set = toHashSet()
        if (!set.containsAll(setOf(2, 3, 4, 5))) {
            return false
        }
        return set.contains(1) || set.contains(6)
    }

    override fun List<Int>.holdDiceFor(): List<Int> {
        val set = toMutableSet()
        set.removeAll { it in 2..5 }
        val result = mutableListOf<Int>()
        for (i in 2..5) {
            if (contains(i)) {
                result.add(i)
            }
        }

        if (set.isEmpty()) {
            return result
        }

        result.add(set.first())
        return result
    }
}

object FullHouseSolver : ComplexCombinationSolver() {
    override fun List<Int>.isValid(): Boolean {
        val possibleCounts = hashSetOf(2, 3, 5)

        val map = groupingBy { it }.eachCount()
        if (map.size > 2)
            return false
        return possibleCounts.contains(map.values.first())
    }

    override fun List<Int>.holdDiceFor(): List<Int> {
        val gist = groupingBy { it }
            .eachCount()
            .map { DiceCount(it.key, it.value) }
            .sortedBy { it.count }
            .reversed()
        if (gist.first().count == 4) {
            return List(4) { gist.first().value }
        }
        if (gist.first().count == 3) {
            return List(3) { gist.first().value } + listOf(gist[1].value)
        }
        if (gist.first().count == 1) {
            return subList(0, 2)
        }
        if (gist[1].count == 2) {
            return List(2) { gist.first().value } + List(2) { gist[1].value }
        }
        return List(2) { gist.first().value } + listOf(gist[1].value)
    }

    private data class DiceCount(
        val value: Int,
        val count: Int
    )
}

object YahtzeeSolver : ComplexCombinationSolver() {
    override fun List<Int>.isValid(): Boolean {
        return toSet().size == 1
    }

    override fun List<Int>.holdDiceFor(): List<Int> {
        return groupingBy { it }
            .eachCount()
            .maxBy { it.value }
            .let { entry -> List(entry.value) { entry.key } }
    }
}

abstract class ComplexCombinationSolver {
    private val powersOfSix = (0..15).map { 6.toDouble().pow(it).toLong() }

    abstract fun List<Int>.isValid(): Boolean

    abstract fun List<Int>.holdDiceFor(): List<Int>

    fun calculate(): Double {
        fun calculateShortStreetInternal(
            combination: List<Int>? = null,
            rerollAttempt: Int = 0,
            diceRolled: Int = 0
        ): Long {
            if (combination?.isValid() == true) {
                return powersOfSix[15 - diceRolled]
            }
            if (rerollAttempt == 3) {
                return 0
            }

            val heldDice = combination?.holdDiceFor() ?: emptyList()
            val diceRerolled = 5 - heldDice.size
            val newCombinations = generateCombinations(diceRerolled).map { heldDice + it }
            val nextRerollAttempt = rerollAttempt + 1
            return newCombinations.map {
                calculateShortStreetInternal(
                    combination = it,
                    rerollAttempt = nextRerollAttempt,
                    diceRolled = diceRolled + diceRerolled
                )
            }.sum()
        }
        return calculateShortStreetInternal().toDouble() / powersOfSix[15]
    }

    private fun generateCombinations(length: Int): Sequence<List<Int>> {
        val range = 1..6

        if (length <= 0) {
            return sequenceOf(emptyList())
        }

        return range.asSequence().flatMap { value ->
            generateCombinations(length - 1).map { list ->
                listOf(value) + list
            }
        }
    }
}
