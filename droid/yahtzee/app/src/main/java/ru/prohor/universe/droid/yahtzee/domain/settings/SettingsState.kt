package ru.prohor.universe.droid.yahtzee.domain.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SettingsState {
    var settings by mutableStateOf(Settings())
        private set

    fun load(context: Context) {
        settings = SettingsStorage.read(context)
    }

    fun save(context: Context) {
        SettingsStorage.write(context, settings)
    }

    fun update(context: Context, transform: (Settings) -> Settings) {
        settings = transform(settings)
        save(context)
    }
}
