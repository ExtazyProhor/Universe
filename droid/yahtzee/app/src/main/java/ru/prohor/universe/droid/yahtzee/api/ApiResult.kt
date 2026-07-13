package ru.prohor.universe.droid.yahtzee.api

sealed interface ApiResult {
    data object Success : ApiResult

    data class Error(val message: String) : ApiResult
}
