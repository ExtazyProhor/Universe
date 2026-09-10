package ru.prohor.universe.padawan.kotlin.scripts

fun main() {
    findValidCombinations()
}

fun findValidCombinations() {
    val flagsByCenter = Flag.entries.groupBy { it.centerColor }
    val activeCenters = flagsByCenter.keys.toList()

    val currentMatch = mutableListOf<Flag>()
    val cubeStock = CubeStock()

    println("Начинаем поиск валидных комбинаций...")
    backtrack(0, activeCenters, flagsByCenter, cubeStock, currentMatch)
}

private fun backtrack(
    centerIndex: Int,
    centers: List<Color>,
    flagsByCenter: Map<Color, List<Flag>>,
    stock: CubeStock,
    currentCombination: MutableList<Flag>
) {
    if (centerIndex == centers.size) {
        if (currentCombination.size >= 4) {
            println("Найдена комбинация (${currentCombination.size} фл.): ${currentCombination.joinToString(", ")}")
        }
        return
    }

    val currentCenterColor = centers[centerIndex]
    val availableFlags = flagsByCenter[currentCenterColor] ?: emptyList()

    for (flag in availableFlags) {
        if (stock.applyFlag(flag)) {
            currentCombination.add(flag)
            backtrack(centerIndex + 1, centers, flagsByCenter, stock, currentCombination)
            currentCombination.removeAt(currentCombination.lastIndex)
            stock.removeFlag(flag)
        }
    }
    backtrack(centerIndex + 1, centers, flagsByCenter, stock, currentCombination)
}

class CubeStock {
    val stock = mutableMapOf<Pair<Color, SquareType>, Int>()

    init {
        for (color in Color.entries) {
            stock[Pair(color, SquareType.CENTER)] = 1
            stock[Pair(color, SquareType.EDGE)] = 4
            stock[Pair(color, SquareType.CORNER)] = 4
        }
    }

    // пытается применить флаг, возвращает false, если деталей не хватило
    fun applyFlag(flag: Flag): Boolean {
        for (req in flag.squares) {
            val current = stock[Pair(req.color, req.type)] ?: 0
            if (current < req.count) return false
        }
        for (req in flag.squares) {
            val key = Pair(req.color, req.type)
            stock[key] = stock[key]!! - req.count
        }
        return true
    }

    // возвращает детали обратно при шаге назад (backtracking)
    fun removeFlag(flag: Flag) {
        for (req in flag.squares) {
            val key = Pair(req.color, req.type)
            stock[key] = stock[key]!! + req.count
        }
    }
}

/** 🇦🇿 🇮🇪 🇷🇺 🇧🇬
 * 🟥 На КРАСНОМ центре
 * - 🇯🇵 Япония
 * - 🇧🇩 Бангладеш
 * - 🏴󠁧󠁢󠁥󠁮󠁧󠁿 Англия / 🇬🇪 Грузия
 * - 🇦🇿 Азербайджан
 * - 🇮🇸 Исландия
 * ⬜ На БЕЛОМ центре
 * - 🇦🇹 Австрия
 * - 🇳🇬 Нигерия
 * - 🇫🇷 Франция
 * - 🇮🇪 Ирландия
 * - 🇮🇹 Италия
 * - 🇸🇱 Сьерра-Леоне
 * - 🇩🇰 Дания
 * 🟦 На СИНЕМ центре
 * - 🇦🇲 Армения
 * - 🇷🇺 Россия
 * - 🇻🇪 Венесуэла
 * - 🇳🇴 Норвегия
 * 🟩 На ЗЕЛЕНОМ центре
 * - 🇧🇬 Болгария
 * - 🇱🇹 Литва
 * 🟨 На ЖЕЛТОМ центре
 * - 🇪🇸 Испания
 * - 🇷🇴 Румыния
 * - 🇬🇦 Габон
 * - 🇬🇳 Гвинея
 * - 🇸🇪 Швеция
 * */
enum class Flag(val centerColor: Color, val squares: List<Squares>) {
    JAPAN(
        Color.RED, listOf(
            Squares(1, SquareType.CENTER, Color.RED),
            Squares(4, SquareType.EDGE, Color.WHITE),
            Squares(4, SquareType.CORNER, Color.WHITE)
        )
    ),
    BANGLADESH(
        Color.RED, listOf(
            Squares(1, SquareType.CENTER, Color.RED),
            Squares(4, SquareType.EDGE, Color.GREEN),
            Squares(4, SquareType.CORNER, Color.GREEN)
        )
    ),
    ENGLAND(
        Color.RED, listOf(
            Squares(1, SquareType.CENTER, Color.RED),
            Squares(4, SquareType.EDGE, Color.RED),
            Squares(4, SquareType.CORNER, Color.WHITE)
        )
    ),
    AZERBAIJAN(
        Color.RED, listOf(
            Squares(1, SquareType.CENTER, Color.RED), Squares(2, SquareType.EDGE, Color.RED),
            Squares(1, SquareType.EDGE, Color.BLUE), Squares(2, SquareType.CORNER, Color.BLUE),
            Squares(1, SquareType.EDGE, Color.GREEN), Squares(2, SquareType.CORNER, Color.GREEN)
        )
    ),
    ICELAND(
        Color.RED, listOf(
            Squares(1, SquareType.CENTER, Color.RED),
            Squares(4, SquareType.EDGE, Color.RED),
            Squares(4, SquareType.CORNER, Color.BLUE)
        )
    ),

    AUSTRIA(
        Color.WHITE, listOf(
            Squares(1, SquareType.CENTER, Color.WHITE), Squares(2, SquareType.EDGE, Color.WHITE),
            Squares(2, SquareType.EDGE, Color.RED), Squares(4, SquareType.CORNER, Color.RED)
        )
    ),
    NIGERIA(
        Color.WHITE, listOf(
            Squares(1, SquareType.CENTER, Color.WHITE), Squares(2, SquareType.EDGE, Color.WHITE),
            Squares(2, SquareType.EDGE, Color.GREEN), Squares(4, SquareType.CORNER, Color.GREEN)
        )
    ),
    FRANCE(
        Color.WHITE, listOf(
            Squares(1, SquareType.CENTER, Color.WHITE), Squares(2, SquareType.EDGE, Color.WHITE),
            Squares(1, SquareType.EDGE, Color.BLUE), Squares(2, SquareType.CORNER, Color.BLUE),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED)
        )
    ),
    IRELAND(
        Color.WHITE, listOf(
            Squares(1, SquareType.CENTER, Color.WHITE), Squares(2, SquareType.EDGE, Color.WHITE),
            Squares(1, SquareType.EDGE, Color.GREEN), Squares(2, SquareType.CORNER, Color.GREEN),
            Squares(1, SquareType.EDGE, Color.ORANGE), Squares(2, SquareType.CORNER, Color.ORANGE)
        )
    ),
    ITALY(
        Color.WHITE, listOf(
            Squares(1, SquareType.CENTER, Color.WHITE), Squares(2, SquareType.EDGE, Color.WHITE),
            Squares(1, SquareType.EDGE, Color.GREEN), Squares(2, SquareType.CORNER, Color.GREEN),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED)
        )
    ),
    SIERRA_LEONE(
        Color.WHITE, listOf(
            Squares(1, SquareType.CENTER, Color.WHITE), Squares(2, SquareType.EDGE, Color.WHITE),
            Squares(1, SquareType.EDGE, Color.GREEN), Squares(2, SquareType.CORNER, Color.GREEN),
            Squares(1, SquareType.EDGE, Color.BLUE), Squares(2, SquareType.CORNER, Color.BLUE)
        )
    ),
    DENMARK(
        Color.WHITE, listOf(
            Squares(1, SquareType.CENTER, Color.WHITE),
            Squares(4, SquareType.EDGE, Color.WHITE),
            Squares(4, SquareType.CORNER, Color.RED)
        )
    ),
    ARMENIA(
        Color.BLUE, listOf(
            Squares(1, SquareType.CENTER, Color.BLUE), Squares(2, SquareType.EDGE, Color.BLUE),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED),
            Squares(1, SquareType.EDGE, Color.ORANGE), Squares(2, SquareType.CORNER, Color.ORANGE)
        )
    ),
    RUSSIA(
        Color.BLUE, listOf(
            Squares(1, SquareType.CENTER, Color.BLUE), Squares(2, SquareType.EDGE, Color.BLUE),
            Squares(1, SquareType.EDGE, Color.WHITE), Squares(2, SquareType.CORNER, Color.WHITE),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED)
        )
    ),
    VENEZUELA(
        Color.BLUE, listOf(
            Squares(1, SquareType.CENTER, Color.BLUE), Squares(2, SquareType.EDGE, Color.BLUE),
            Squares(1, SquareType.EDGE, Color.YELLOW), Squares(2, SquareType.CORNER, Color.YELLOW),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED)
        )
    ),
    NORWAY(
        Color.BLUE, listOf(
            Squares(1, SquareType.CENTER, Color.BLUE),
            Squares(4, SquareType.EDGE, Color.BLUE),
            Squares(4, SquareType.CORNER, Color.RED)
        )
    ),

    BULGARIA(
        Color.GREEN, listOf(
            Squares(1, SquareType.CENTER, Color.GREEN), Squares(2, SquareType.EDGE, Color.GREEN),
            Squares(1, SquareType.EDGE, Color.WHITE), Squares(2, SquareType.CORNER, Color.WHITE),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED)
        )
    ),
    LITHUANIA(
        Color.GREEN, listOf(
            Squares(1, SquareType.CENTER, Color.GREEN), Squares(2, SquareType.EDGE, Color.GREEN),
            Squares(1, SquareType.EDGE, Color.YELLOW), Squares(2, SquareType.CORNER, Color.YELLOW),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED)
        )
    ),

    SPAIN(
        Color.YELLOW, listOf(
            Squares(1, SquareType.CENTER, Color.YELLOW), Squares(2, SquareType.EDGE, Color.YELLOW),
            Squares(2, SquareType.EDGE, Color.RED), Squares(4, SquareType.CORNER, Color.RED)
        )
    ),
    ROMANIA(
        Color.YELLOW, listOf(
            Squares(1, SquareType.CENTER, Color.YELLOW), Squares(2, SquareType.EDGE, Color.YELLOW),
            Squares(1, SquareType.EDGE, Color.BLUE), Squares(2, SquareType.CORNER, Color.BLUE),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED)
        )
    ),
    GABON(
        Color.YELLOW, listOf(
            Squares(1, SquareType.CENTER, Color.YELLOW), Squares(2, SquareType.EDGE, Color.YELLOW),
            Squares(1, SquareType.EDGE, Color.GREEN), Squares(2, SquareType.CORNER, Color.GREEN),
            Squares(1, SquareType.EDGE, Color.BLUE), Squares(2, SquareType.CORNER, Color.BLUE)
        )
    ),
    GUINEA(
        Color.YELLOW, listOf(
            Squares(1, SquareType.CENTER, Color.YELLOW), Squares(2, SquareType.EDGE, Color.YELLOW),
            Squares(1, SquareType.EDGE, Color.RED), Squares(2, SquareType.CORNER, Color.RED),
            Squares(1, SquareType.EDGE, Color.GREEN), Squares(2, SquareType.CORNER, Color.GREEN)
        )
    ),
    SWEDEN(
        Color.YELLOW, listOf(
            Squares(1, SquareType.CENTER, Color.YELLOW),
            Squares(4, SquareType.EDGE, Color.YELLOW),
            Squares(4, SquareType.CORNER, Color.BLUE)
        )
    );
}

data class Squares(
    val count: Int,
    val type: SquareType,
    val color: Color
)

enum class SquareType {
    CENTER, EDGE, CORNER
}

enum class Color {
    WHITE, YELLOW, RED, ORANGE, BLUE, GREEN
}
