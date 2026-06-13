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
            .take(TOP)
    }

    fun registerTemplates(context: Context) {
        TeamsState.teams().forEach { register(it) }

        templates = templates
            .sortedByDescending { it.usages }
            .take(100)
            .toMutableStateList()
        TeamTemplatesStorage.write(context, templates)
    }

    private fun register(team: Team) {
        val index = templates.indexOfFirst { it.name == team.name && it.color == team.color }

        if (index >= 0) {
            val current = templates[index]
            templates[index] = current.copy(usages = current.usages + 1)
        } else {
            templates += TeamTemplate(team.name, team.color, 1)
        }
    }
}
