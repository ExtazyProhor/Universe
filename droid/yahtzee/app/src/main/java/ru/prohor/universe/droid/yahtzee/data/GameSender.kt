package ru.prohor.universe.droid.yahtzee.data

import android.content.Context
import ru.prohor.universe.droid.yahtzee.state.SavedGamesState

object GameSender {
    fun send(context: Context, uuid: String) {
        // TODO отправка на сервер

        SavedGamesState.remove(uuid, context)
    }

    fun sendAll(context: Context) {
        // TODO отправка на сервер

        SavedGamesState.removeAll(context)
    }
}
