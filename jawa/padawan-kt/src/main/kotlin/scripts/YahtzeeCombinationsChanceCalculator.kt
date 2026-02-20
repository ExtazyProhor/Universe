package ru.prohor.universe.kt.padawan.scripts

import kotlin.math.pow
import kotlin.system.measureTimeMillis

fun main() {
    // time: 78 ms!
    // time: 231 ms!
    // time: 132100 ms!
    val time = measureTimeMillis {
        println(ShortStreetSolver.calculate()) // 0.6025279139347071 / 0.6025279139347077
        println(LongStreetSolver.calculate()) // 0.2491031974269013 / 0.24910319742690157
        println(FullHouseSolver.calculate()) // 0.3588891746684954 / 0.3588891746684957
        println(YahtzeeSolver.calculate()) // 0.04602864252569886 / 0.04602864252569899
    }
    println("time: $time ms!")
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
    private val cache = HashMap<State, Double>()

    abstract fun List<Int>.isValid(): Boolean

    abstract fun List<Int>.holdDiceFor(): List<Int>

    fun calculate(): Double {
        fun calculateInternal(
            combination: List<Int>? = null,
            rerollAttempt: Int = 0
        ): Double {
            val state = State(combination, rerollAttempt)
            cache[state]?.let { return it }
            if (combination?.isValid() == true) {
                return 1.0
            }
            if (rerollAttempt == 3) {
                return 0.0
            }

            val heldDice = combination?.holdDiceFor() ?: emptyList()
            val diceRerolled = 5 - heldDice.size
            val newCombinations = generatedCombinations[diceRerolled]
            val nextRerollAttempt = rerollAttempt + 1
            return newCombinations.sumOf { roll ->
                val probability = roll.weight.toDouble() / powersOfSix[diceRerolled]
                probability * calculateInternal(
                    combination = heldDice + roll.values,
                    rerollAttempt = nextRerollAttempt
                )
            }.also { cache[state] = it }
        }
        return calculateInternal()
    }

    private data class State(
        val combination: List<Int>?,
        val rerollAttempt: Int
    )

    companion object {
        private val powersOfSix = (0..15).map { 6.toDouble().pow(it).toLong() }
    }
}

private val generatedCombinations = (0..5).map { length ->
    generateCombinations(length)
        .map { it.sorted() }
        .groupingBy { it }
        .eachCount()
        .map { (reroll, count) -> Roll(reroll, count) }
}

private data class Roll(
    val values: List<Int>,
    val weight: Int
)

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
