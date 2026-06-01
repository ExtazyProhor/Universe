package ru.prohor.universe.droid.yahtzee.data

import android.content.Context
import ru.prohor.universe.droid.yahtzee.api.ApiResult
import ru.prohor.universe.droid.yahtzee.api.YahtzeeApi
import ru.prohor.universe.droid.yahtzee.auth.Auth
import ru.prohor.universe.droid.yahtzee.state.SavedGamesState

object GameSender {
    suspend fun sendAll(context: Context): ApiResult {
        val userKey = Auth.key() ?: return ApiResult.Error("Ключ отсутствует")

        GameStorage.findSavedGames(context).forEach { game ->
            val result = YahtzeeApi.sendGame(
                game = game.content,
                userKey = userKey
            )
            if (result is ApiResult.Error) return result

            SavedGamesState.remove(game.uuid, context)
        }
        return ApiResult.Success
    }
}
