package ru.prohor.universe.droid.yahtzee.domain.game

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
    SIXES("Шестёрки", 6)
}

sealed interface ComplexCombination : Combination

enum class FreeValueCombination(
    override val readableName: String,
) : FreeValue {
    PAIR("Пара"),
    TWO_PAIRS("Две пары"),
    THREE_OF_KIND("Сет"),
    FOUR_OF_KIND("Каре");

    override fun validate(value: Int) = inRange(value) || value == 0
}

enum class FixedValueCombination(
    override val readableName: String,
    val fixedValue: Int
) : ComplexCombination {
    FULL_HOUSE("Фулл-хаус", 25),
    LOW_STRAIGHT("Короткий стрит", 30),
    HIGH_STRAIGHT("Длинный стрит", 40),
    YAHTZEE("Яцзы", 50)
}

sealed interface FreeValue : ComplexCombination {
    fun validate(value: Int): Boolean
}

object CHANCE : FreeValue {
    override val readableName = "Шанс"

    override val name = "CHANCE"

    override fun validate(value: Int) = inRange(value)
}

val ALL_COMPLEX_COMBINATIONS = listOf(
    FreeValueCombination.entries,
    FixedValueCombination.entries,
    listOf(CHANCE)
).flatten()

val ALL_COMBINATIONS = SimpleCombination.entries + ALL_COMPLEX_COMBINATIONS

sealed interface Combination : CombinationItem {
    val name: String
}

sealed interface CombinationItem {
    val readableName: String
}

private fun inRange(value: Int) = value >= 5 && value <= 30
