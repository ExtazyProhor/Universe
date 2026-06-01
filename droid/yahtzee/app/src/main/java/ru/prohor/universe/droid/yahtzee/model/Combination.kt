package ru.prohor.universe.droid.yahtzee.model

enum class MetaCombination(
    override val readableName: String
) : CombinationItem {
    TOTAL("Сумма очков"),
    SCORE_TO_BONUS("Очков до бонуса")
}

enum class SimpleCombination(
    override val readableName: String,
    val base: Int
) : Combination {
    UNITS("Единицы", 1),
    TWOS("Двойки", 2),
    THREES("Тройки", 3),
    FOURS("Четверки", 4),
    FIVES("Пятерки", 5),
    SIXES("Шестёрки", 6);

    override fun validate(value: Int) = simple(value, base)
}

sealed interface ComplexCombination : Combination

enum class FreeValueCombination(
    override val readableName: String,
) : ComplexCombination {
    PAIR("Пара"),
    TWO_PAIRS("Две пары"),
    THREE_OF_KIND("Сет"),
    FOUR_OF_KIND("Каре");

    override fun validate(value: Int) = freeValue(value)
}

enum class FixedValueCombination(
    override val readableName: String,
    val fixedValue: Int
) : ComplexCombination {
    FULL_HOUSE("Фулл-хаус", 25),
    LOW_STRAIGHT("Короткий стрит", 30),
    HIGH_STRAIGHT("Длинный стрит", 40),
    YAHTZEE("Яцзы", 50);

    override fun validate(value: Int) = zeroOr(value, fixedValue)
}

object CHANCE : ComplexCombination {
    override val readableName = "Шанс"

    override val name = "CHANCE"

    override fun validate(value: Int) = inRange(value, 5, 30)
}

val ALL_COMPLEX_COMBINATIONS = listOf(
    FreeValueCombination.entries,
    FixedValueCombination.entries,
    listOf(CHANCE)
).flatten()

val ALL_COMBINATIONS = SimpleCombination.entries + ALL_COMPLEX_COMBINATIONS

sealed interface Combination : CombinationItem {
    val name: String

    fun validate(value: Int): Boolean
}

sealed interface CombinationItem {
    val readableName: String
}

private fun simple(value: Int, digit: Int) = inRange(value, 0, digit * 5) && value % digit == 0

private fun zeroOr(value: Int, target: Int) = value == 0 || value == target

private fun freeValue(value: Int) = inRange(value, 5, 30) || value == 0

private fun inRange(value: Int, min: Int, max: Int) = value >= min && value <= max
