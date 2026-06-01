package ru.prohor.universe.droid.yahtzee.state

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.prohor.universe.droid.yahtzee.data.GameStorage
import ru.prohor.universe.droid.yahtzee.model.GameDescription
import ru.prohor.universe.droid.yahtzee.model.GamesDescription
import ru.prohor.universe.droid.yahtzee.model.SavedGame

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
