package ru.prohor.universe.droid.yahtzee.model

enum class MetaCombination(
    override val readableName: String
) : CombinationItem {
    TOTAL("Сумма очков"),
    SCORE_TO_BONUS("Очков до бонуса")
}

enum class SimpleCombination(
    override val readableName: String,
    private val validator: (Int) -> Boolean
) : Combination {
    UNITS("Единицы", { value -> simple(value, 1) }),
    TWOS("Двойки", { value -> simple(value, 2) }),
    THREES("Тройки", { value -> simple(value, 3) }),
    FOURS("Четверки", { value -> simple(value, 4) }),
    FIVES("Пятерки", { value -> simple(value, 5) }),
    SIXES("Шестёрки", { value -> simple(value, 6) });

    override fun validate(value: Int) = validator.invoke(value)
}

enum class ComplexCombination(
    override val readableName: String,
    private val validator: (Int) -> Boolean
) : Combination {
    PAIR("Пара", { value -> baseComplex(value) }),
    TWO_PAIRS("Две пары", { value -> baseComplex(value) }),
    THREE_OF_KIND("Сет", { value -> baseComplex(value) }),
    FOUR_OF_KIND("Каре", { value -> baseComplex(value) }),
    FULL_HOUSE("Фулл-хаус", { value -> zeroOr(value, 25) }),
    LOW_STRAIGHT("Короткий стрит", { value -> zeroOr(value, 30) }),
    HIGH_STRAIGHT("Длинный стрит", { value -> zeroOr(value, 40) }),
    YAHTZEE("Яцзы", { value -> zeroOr(value, 50) }),
    CHANCE("Шанс", { value -> inRange(value, 5, 30) });

    override fun validate(value: Int) = validator.invoke(value)
}

sealed interface Combination : CombinationItem {
    fun validate(value: Int): Boolean
}

sealed interface CombinationItem {
    val readableName: String
}

private fun simple(value: Int, digit: Int) = inRange(value, 0, digit * 5) && value % digit == 0

private fun zeroOr(value: Int, target: Int) = value == 0 || value == target

private fun baseComplex(value: Int) = inRange(value, 5, 30) || value == 0

private fun inRange(value: Int, min: Int, max: Int) = value >= min && value <= max
