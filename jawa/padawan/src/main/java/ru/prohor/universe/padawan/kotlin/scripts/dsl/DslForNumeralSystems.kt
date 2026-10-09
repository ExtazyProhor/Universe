package ru.prohor.universe.padawan.kotlin.scripts.dsl

// нужно чтобы был в отдельном пакете чтобы не перебивать kotlin "to" функцию
fun main() {
    5 to 2 p p
}

/**
 * Для простого вывода результата:
 *
 * ```kt
 * 5 to 2 p p // 101
 * ```
 */
infix fun Any?.p(stop: Unit) = print(this)
typealias p = Unit

/**
 * Для перевода из десятичной системы в не-десятичную:
 *
 * ```kt
 * 5 to 2 // 101
 * ```
 */
infix fun Int.to(radix: Int) = toString(radix)

/**
 * Для перевода из не-десятичной системы в десятичную:
 *
 * ```kt
 * "101" from 2 // 5
 * ```
 */
infix fun String.from(radix: Int) = toInt(radix)

/**
 * Для перевода из одной не-десятичной системы в другую не-десятичную:
 *
 * ```kt
 * "101" to 3 from 2 // 12
 * ```
 */
infix fun String.to(radix: Int) = W(this, radix)

data class W(val num: String, val radix: Int)

infix fun W.from(radix: Int) = num.toInt(radix).toString(this.radix)
