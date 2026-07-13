package ru.prohor.universe.droid.yahtzee.domain.storage

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SavedGamesState {
    private var descriptions by mutableStateOf<List<GameDescription>>(emptyList())

    fun initialize(context: Context) {
        descriptions = GameStorage.readDescription(context).games.sortedByDescending { it.finish }
    }

    fun games(): List<GameDescription> {
        return descriptions
    }

    fun save(game: SavedGame, context: Context) {
        GameStorage.save(context, game)
        descriptions = (descriptions + game.description()).sortedByDescending { it.finish }
        persist(context)
    }

    fun remove(uuid: String, context: Context) {
        GameStorage.delete(context, uuid)
        descriptions = descriptions.filterNot { it.uuid == uuid }
        persist(context)
    }

    private fun persist(context: Context) {
        GameStorage.writeDescription(context, GamesDescription(descriptions))
    }
}
