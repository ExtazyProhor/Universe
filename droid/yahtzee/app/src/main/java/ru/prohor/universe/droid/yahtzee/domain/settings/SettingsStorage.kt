package ru.prohor.universe.droid.yahtzee.domain.settings

import android.content.Context
import ru.prohor.universe.droid.yahtzee.ext.JsonMapper
import java.io.File

object SettingsStorage {
    fun read(context: Context): Settings {
        val file = file(context)
        if (!file.exists()) return Settings()

        return runCatching {
            JsonMapper.decode<Settings>(file.readText())
        }.getOrDefault(Settings())
    }

    fun write(context: Context, settings: Settings) {
        file(context).writeText(JsonMapper.encode(settings))
    }

    private fun file(context: Context) = File(context.filesDir, "settings.json")
}
