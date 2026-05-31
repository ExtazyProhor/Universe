package ru.prohor.universe.droid.yahtzee.auth

sealed interface AuthResult {
    data object Success : AuthResult

    data class Error(val message: String) : AuthResult
}
