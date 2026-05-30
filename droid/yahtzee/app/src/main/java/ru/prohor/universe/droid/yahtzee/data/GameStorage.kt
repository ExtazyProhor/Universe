package ru.prohor.universe.droid.yahtzee.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.prohor.universe.droid.yahtzee.model.GamesDescription
import ru.prohor.universe.droid.yahtzee.model.SavedGame
import java.io.File

object GameStorage {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun save(context: Context, game: SavedGame) {
        val directory = games(context)
        directory.mkdirs()

        val file = game(directory, game.uuid)
        val content = json.encodeToString(game)
        file.writeText(content)
    }

    fun loadAll(context: Context): List<SavedGame> {
        val directory = games(context)
        if (!directory.exists()) return emptyList()

        return directory.listFiles()?.mapNotNull { file ->
            runCatching { json.decodeFromString<SavedGame>(file.readText()) }.getOrNull()
        }?.sortedByDescending { it.finish } ?: emptyList()
    }

    fun delete(context: Context, uuid: String) {
        game(games(context), uuid).delete()
    }

    fun readDescription(context: Context): GamesDescription {
        val file = description(context)
        if (!file.exists()) return GamesDescription()

        return runCatching {
            json.decodeFromString<GamesDescription>(file.readText())
        }.getOrNull() ?: GamesDescription()
    }

    fun writeDescription(context: Context, description: GamesDescription) {
        val file = description(context)
        val content = json.encodeToString(description)
        file.writeText(content)
    }

    private fun game(games: File, uuid: String) = File(games, "$uuid.json")

    private fun games(context: Context) = File(context.filesDir, "games")

    private fun description(context: Context) = File(context.filesDir, "games/description.json")
}
