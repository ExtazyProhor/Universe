package ru.prohor.universe.kt.padawan.scripts

import kotlin.math.abs
import kotlin.math.pow
import kotlin.system.measureTimeMillis

fun main() {
    val solvers = listOf(
        PairSolver,
        ThreeOfKindSolver,
        FourOfKindSolver,
        FullHouseSolver,
        ShortStreetSolver,
        LongStreetSolver,
        YahtzeeSolver
    )
    val sum = solvers.sumOf { solver ->
        var p: Double
        val time = measureTimeMillis {
            p = solver.calculate() * 100
        }
        println(
            "${solver::class.simpleName?.removeSuffix("Solver")}: time = $time ms, p = $p %"
        )
        time
    }
    println("sum millis = $sum")
    test()
}

fun test() {
    compare(0.9992061677589775, PairSolver.calculate())
    compare(0.7431952700299738, ThreeOfKindSolver.calculate())
    compare(0.2907935835069176, FourOfKindSolver.calculate())
    compare(0.3588891746684954, FullHouseSolver.calculate())
    compare(0.6025279139347071, ShortStreetSolver.calculate())
    compare(0.2491031974269013, LongStreetSolver.calculate())
    compare(0.04602864252569886, YahtzeeSolver.calculate())
}

fun compare(expected: Double, actual: Double) {
    if (abs(expected - actual) >= 1e-13) {
        throw RuntimeException("Illegal value: expected=$expected, actual=$actual")
    }
}

object PairSolver : SameDiceCombinationSolver() {
    override val count = 2
}

object ThreeOfKindSolver : SameDiceCombinationSolver() {
    override val count = 3
}

object FourOfKindSolver : SameDiceCombinationSolver() {
    override val count = 4
}

object FullHouseSolver : CombinationSolver() {
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

object ShortStreetSolver : CombinationSolver() {
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

object LongStreetSolver : CombinationSolver() {
    override fun List<Int>.isValid(): Boolean {
        val c = counts()
        for (i in 2..5) {
            if (c[i] == 0) {
                return false
            }
        }
        return c[1] > 0 || c[6] > 0
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

object YahtzeeSolver : SameDiceCombinationSolver() {
    override val count = 5
}

abstract class SameDiceCombinationSolver : CombinationSolver() {
    abstract val count: Int

    override fun List<Int>.isValid(): Boolean {
        val counts = counts()
        for (i in 1..6)
            if (counts[i] >= count)
                return true
        return false
    }

    override fun List<Int>.holdDiceFor(): List<Int> {
        val counts = counts()
        var bestValue = 1
        var bestCount = counts[1]

        for (i in 2..6) {
            if (counts[i] > bestCount) {
                bestValue = i
                bestCount = counts[i]
            }
        }
        return List(bestCount) { bestValue }
    }
}

abstract class CombinationSolver {
    private val cache = HashMap<State, Double>()

    abstract fun List<Int>.isValid(): Boolean

    abstract fun List<Int>.holdDiceFor(): List<Int>

    fun calculate(): Double {
        return calculateInternal()
    }

    fun calculate(combination: List<Int>, roll: Int): Double {
        return calculateInternal(combination, roll)
    }

    fun calculateInternal(
        combination: List<Int>? = null,
        rerollAttempt: Int = 0
    ): Double {
        val state = State(encode(combination), rerollAttempt)
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

    private data class State(
        val encoded: Int,
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
    if (length <= 0) {
        return sequenceOf(emptyList())
    }

    return (1..6).asSequence().flatMap { value ->
        generateCombinations(length - 1).map { list ->
            listOf(value) + list
        }
    }
}

private fun encode(dice: List<Int>?): Int {
    if (dice == null) return -1
    var result = 0
    dice.sorted().forEachIndexed { i, v ->
        result = result or (v shl (i * 3))
    }
    return result
}

private fun List<Int>.counts(): IntArray {
    val counts = IntArray(7)
    for (v in this) counts[v]++
    return counts
}
