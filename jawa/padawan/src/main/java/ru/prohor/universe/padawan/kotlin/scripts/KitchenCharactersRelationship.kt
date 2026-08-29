package ru.prohor.universe.padawan.kotlin.scripts

fun main() {
    val relations = mutableListOf<Relation>()

    val maxim = Character("Максим Лавров")
    val vika = Character("Виктория Сергеевна")
    val nastya = Character("Официантка Настя")
    val kostya = Character("Бармен Костя")
    val sasha = Character("Официантка Саша")
    val tanya = Character("Татьяна Гончарова")
    val alice = Character("Алиса, дочь Шефа")
    val chief = Character("Шеф")

    relations.add(maxim sleepsWith vika)
    relations.add(maxim kiss nastya)
    relations.add(kostya sleepsWith nastya)
    relations.add(maxim sleepsWith sasha)
    relations.add(tanya kinTo alice)
    relations.add(chief kinTo alice)
    relations.add(chief sleepsWith tanya)
    relations.add(tanya kinTo vika)
}

data class Relation(
    val pair: Pair<Character, Character>,
    val type: RelationType
)

data class Character(
    val name: String
)

enum class RelationType {
    INTIMACY,
    KISS,
    KINSHIP
}

infix fun Character.sleepsWith(other: Character): Relation = Relation(this to other, RelationType.INTIMACY)
infix fun Character.kiss(other: Character): Relation = Relation(this to other, RelationType.KISS)
infix fun Character.kinTo(other: Character): Relation = Relation(this to other, RelationType.KINSHIP)
