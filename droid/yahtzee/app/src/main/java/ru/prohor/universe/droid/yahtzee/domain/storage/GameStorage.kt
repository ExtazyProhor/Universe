package ru.prohor.universe.droid.yahtzee.domain.storage

import android.content.Context
import ru.prohor.universe.droid.yahtzee.ext.JsonMapper
import java.io.File

object GameStorage {
    fun save(context: Context, game: SavedGame) {
        val file = game(context, game.uuid)
        val content = JsonMapper.encode(game)
        file.writeText(content)
    }

    fun findSavedGames(context: Context): Sequence<SavedFile> {
        return games(context).walkTopDown()
            .filter { it.isFile && it.name != "description.json" }
            .map {
                SavedFile(
                    content = it.readText(),
                    uuid = it.name.removeSuffix(".json")
                )
            }
    }

    fun delete(context: Context, uuid: String) {
        game(context, uuid).delete()
    }

    fun readDescription(context: Context): GamesDescription {
        val file = description(context)
        if (!file.exists()) return GamesDescription()

        return runCatching {
            JsonMapper.decode<GamesDescription>(file.readText())
        }.getOrNull() ?: GamesDescription()
    }

    fun writeDescription(context: Context, description: GamesDescription) {
        val file = description(context)
        val content = JsonMapper.encode(description)
        file.writeText(content)
    }

    private fun games(context: Context): File {
        val games = File(context.filesDir, "games")
        games.mkdirs()
        return games
    }

    private fun game(context: Context, uuid: String) = File(games(context), "$uuid.json")

    private fun description(context: Context) = File(games(context), "description.json")
}
