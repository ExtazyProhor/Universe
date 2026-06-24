package ru.prohor.universe.droid.yahtzee.ext

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonMapper {
    val JSON = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    inline fun <reified T> encode(value: T): String {
        return JSON.encodeToString(value)
    }

    inline fun <reified T> decode(string: String): T {
        return JSON.decodeFromString(string)
    }
}
