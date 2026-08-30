package ru.prohor.universe.padawan.kotlin.scripts

fun main() {
    val maxim = Character("Максим Лавров")
    val vika = Character("Виктория Сергеевна")
    val nastya = Character("Официантка Настя")
    val kostya = Character("Бармен Костя")
    val sasha = Character("Официантка Саша")
    val tanya = Character("Татьяна Гончарова")
    val alice = Character("Алиса, дочь Шефа")
    val chief = Character("Шеф")
    val nagiev = Character("Нагиев")
    val kristina = Character("Кристина")
    val nastyaFather = Character("Папа Насти")
    val nastyaMother = Character("Мама Насти")
    val elenaPavlovna = Character("Елена Павловна")

    val relations = listOf(
        maxim sleepsWith vika,
        maxim kiss nastya,
        kostya sleepsWith nastya,
        maxim sleepsWith sasha,
        tanya kinTo alice,
        chief kinTo alice,
        chief sleepsWith tanya,
        tanya kinTo vika,
        nagiev sleepsWith kristina,
        nastyaFather kinTo nastya,
        nastyaMother kinTo nastya,
        nastyaFather sleepsWith nastyaMother,
        chief kiss elenaPavlovna,
    )
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
