package ru.prohor.universe.padawan.kotlin.scripts

private val APPLE_LATIN_TO_RUS_MAP = mapOf(
    '`' to ']', '~' to '[',
    '\\' to 'ё', '|' to 'Ё',
    'q' to 'й', 'Q' to 'Й',
    'w' to 'ц', 'W' to 'Ц',
    'e' to 'у', 'E' to 'У',
    'r' to 'к', 'R' to 'К',
    't' to 'е', 'T' to 'Е',
    'y' to 'н', 'Y' to 'Н',
    'u' to 'г', 'U' to 'Г',
    'i' to 'ш', 'I' to 'Ш',
    'o' to 'щ', 'O' to 'Щ',
    'p' to 'з', 'P' to 'З',
    '[' to 'х', '{' to 'Х',
    ']' to 'ъ', '}' to 'Ъ',
    'a' to 'ф', 'A' to 'Ф',
    's' to 'ы', 'S' to 'Ы',
    'd' to 'в', 'D' to 'В',
    'f' to 'а', 'F' to 'А',
    'g' to 'п', 'G' to 'П',
    'h' to 'р', 'H' to 'Р',
    'j' to 'о', 'J' to 'О',
    'k' to 'л', 'K' to 'Л',
    'l' to 'д', 'L' to 'Д',
    ';' to 'ж', ':' to 'Ж',
    '\'' to 'э', '"' to 'Э',
    'z' to 'я', 'Z' to 'Я',
    'x' to 'ч', 'X' to 'Ч',
    'c' to 'с', 'C' to 'С',
    'v' to 'м', 'V' to 'М',
    'b' to 'и', 'B' to 'И',
    'n' to 'т', 'N' to 'Т',
    'm' to 'ь', 'M' to 'Ь',
    ',' to 'б', '<' to 'Б',
    '.' to 'ю', '>' to 'Ю',
    '&' to '.', '^' to ',',
    '*' to ';', '%' to ':',
    '$' to '%', '#' to '№',
    '@' to '"'
)

// Expected :[!"№%:,.;()_+
// Actual   :[!@#$%,.;()_+

private val APPLE_RUS_TO_LATIN_MAP = APPLE_LATIN_TO_RUS_MAP.entries.associate { (key, value) -> value to key }

enum class KeyboardLayout(
    val map: Map<Char, Char>
) {
    APPLE_RUS_TO_LATIN(APPLE_RUS_TO_LATIN_MAP),
    APPLE_LATIN_TO_RUS(APPLE_LATIN_TO_RUS_MAP);
}

fun convert(input: String, layout: KeyboardLayout): String {
    val map = layout.map
    return input.map { char -> map[char] ?: char }.joinToString("")
}

fun main() {
    println(convert("qwerty", KeyboardLayout.APPLE_LATIN_TO_RUS))
}
