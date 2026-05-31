package ru.prohor.universe.droid.yahtzee.data

import android.content.Context
import ru.prohor.universe.droid.yahtzee.api.YahtzeeApi
import ru.prohor.universe.droid.yahtzee.api.ApiResult
import ru.prohor.universe.droid.yahtzee.auth.Auth
import ru.prohor.universe.droid.yahtzee.state.SavedGamesState

object GameSender {
    suspend fun send(context: Context, uuid: String): ApiResult {
        return sendInternal(context, uuid)
    }

    suspend fun sendAll(context: Context): ApiResult {
        SavedGamesState.games().forEach {
            val result = sendInternal(context, it.uuid)
            if (result is ApiResult.Error) return result
        }
        return ApiResult.Success
    }

    private suspend fun sendInternal(context: Context, uuid: String): ApiResult {
        val game = GameStorage.load(context, uuid) ?: return ApiResult.Error("Игра не найдена")
        val userKey = Auth.key() ?: return ApiResult.Error("Ключ отсутствует")

        val result = YahtzeeApi.sendGame(
            game = game,
            userKey = userKey
        )

        if (result is ApiResult.Success) {
            SavedGamesState.remove(uuid, context)
        }
        return result
    }
}
