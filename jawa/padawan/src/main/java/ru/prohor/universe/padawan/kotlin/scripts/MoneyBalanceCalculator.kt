package ru.prohor.universe.padawan.kotlin.scripts

import ru.prohor.universe.padawan.Padawan
import ru.prohor.universe.padawan.TestFile

fun main() {
    Padawan.readLines(TestFile.TXT)
        .filter { it.startsWith("+") || it.startsWith("-") }
        .joinToString(separator = "") { it.substringBefore(' ') }
        .let { println(it) }
}
