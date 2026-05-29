package ru.prohor.universe.droid.yahtzee.common

inline fun <T> T.letIf(condition: Boolean, block: (T) -> T): T {
    return if (condition) block(this) else this
}
