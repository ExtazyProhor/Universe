package ru.prohor.universe.droid.yahtzee.ext

inline fun <T> T.letIf(condition: Boolean, block: (T) -> T): T {
    return if (condition) block(this) else this
}
