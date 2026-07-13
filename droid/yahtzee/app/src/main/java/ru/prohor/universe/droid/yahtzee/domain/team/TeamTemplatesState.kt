package ru.prohor.universe.droid.yahtzee.domain.team

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import ru.prohor.universe.droid.yahtzee.domain.storage.TeamTemplatesStorage

object TeamTemplatesState {
    private const val TOP = 5

    private var templates = mutableStateListOf<TeamTemplate>()

    fun initialize(context: Context) {
        templates.clear()
        templates.addAll(TeamTemplatesStorage.read(context))
    }

    fun isSuitableTemplatesPresent() = topTemplates().isNotEmpty()

    fun topTemplates(): List<TeamTemplate> {
        val usedColors = TeamsState.usedColors()
        val usedNames = TeamsState.usedNames()

        return templates
            .filter { it.color !in usedColors }
            .filter { it.name !in usedNames }
            .sortedByDescending { it.usages }
            .distinctBy { it.name.lowercase() }
            .distinctBy { it.color }
            .take(TOP)
    }

    fun register(name: String, color: TeamColor, context: Context) {
        val index = templates.indexOfFirst { it.name == name && it.color == color }

        if (index >= 0) {
            val current = templates[index]
            templates[index] = current.copy(usages = current.usages + 1)
        } else {
            templates += TeamTemplate(name, color, 1)
        }

        templates = templates
            .sortedByDescending { it.usages }
            .take(100)
            .toMutableStateList()

        TeamTemplatesStorage.write(context, templates)
    }
}
