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
    val sonOfElenaPavlovna = Character("Сын Елены Павловны")
    val ilya = Character("Официант Илья")
    val katya = Character("Катя, дочка Шефа")
    val leva = Character("Лёва")
    val momOfLeva = Character("Мама Лёвы")
    val hostessAngelina = Character("Хостес Ангелина")
    val eva = Character("Официантка Ева")
    val denis = Character("Денис")
    val herman = Character("Герман")
    val aynura = Character("Айнура")
    val oksana = Character("Оксана Смирнова")
    val eleonora = Character("Элеонора Андреевна")
    val senya = Character("Сеня")
    val fedya = Character("Федя")
    val wifeOfSenya = Character("Жена Сени")

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
        chief sleepsWith elenaPavlovna,
        ilya sleepsWith sasha,
        sonOfElenaPavlovna kinTo elenaPavlovna,
        leva kinTo momOfLeva,
        leva kiss katya,
        katya kinTo chief,
        maxim kiss hostessAngelina,
        maxim kiss katya,
        nagiev kiss eva,
        maxim kiss eva,
        denis sleepsWith katya,
        herman sleepsWith aynura,
        chief sleepsWith eva,
        nagiev sleepsWith oksana,
        denis sleepsWith eleonora,
        chief sleepsWith eleonora,
        senya sleepsWith wifeOfSenya,
        fedya kiss wifeOfSenya,
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
