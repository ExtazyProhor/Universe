package ru.prohor.universe.padawan.kotlin.scripts

import ru.prohor.universe.jocasta.core.security.PasswordGenerator

fun main() {
    val generator = PasswordGenerator()
    println(generator.generate())
}
