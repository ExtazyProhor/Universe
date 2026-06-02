package ru.prohor.universe.droid.yahtzee.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.prohor.universe.droid.yahtzee.model.TeamTemplate
import java.io.File

object TeamTemplatesStorage {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun read(context: Context): List<TeamTemplate> {
        val file = file(context)
        if (!file.exists()) return emptyList()

        return runCatching {
            json.decodeFromString<List<TeamTemplate>>(file.readText())
        }.getOrNull() ?: emptyList()
    }

    fun write(context: Context, templates: List<TeamTemplate>) {
        val content = json.encodeToString(templates)
        file(context).writeText(content)
    }

    private fun file(context: Context): File {
        return File(context.filesDir, "team_templates.json")
    }
}
