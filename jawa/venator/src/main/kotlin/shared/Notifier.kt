package ru.prohor.universe.venator.shared

interface Notifier {
    fun failure(message: String)

    fun info(message: String)

    fun success(message: String)
}
