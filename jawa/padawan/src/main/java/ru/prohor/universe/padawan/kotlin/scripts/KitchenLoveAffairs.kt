package ru.prohor.universe.padawan.kotlin.scripts

import ru.prohor.universe.padawan.kotlin.scripts.LoveAffairType.*

fun main() {
    val affairs = mutableListOf<LoveAffair>()

    val maxim = Character("Максим Лавров")
    val vika = Character("Виктория Сергеевна")
    val nastya = Character("Официантка Настя")
    val kostya = Character("Бармен Костя")

    affairs.add(LoveAffair(maxim to vika, INTERCOURSE))
    affairs.add(LoveAffair(maxim to nastya, KISS))
    affairs.add(LoveAffair(kostya to nastya, INTERCOURSE))
}

data class LoveAffair(
    val pair: Pair<Character, Character>,
    val type: LoveAffairType
)

data class Character(
    val name: String
)

enum class LoveAffairType {
    INTERCOURSE,
    KISS
}
