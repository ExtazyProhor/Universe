package ru.prohor.universe.uni.cli

import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.clikt.core.parse
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parsers.CommandLineParser
import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.rendering.TextColors.gray
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.terminal.prompt
import com.github.ajalt.mordant.widgets.SelectList

internal class InteractiveMenu(private val root: Uni) {
    private val terminal = root.terminal

    fun run() {
        val path = mutableListOf<CoreCliktCommand>(root)
        while (true) {
            val current = path.last()
            val children = current.registeredSubcommands().filterNot { it.hiddenFromHelp }
            val back = red("← Back")
            val exit = red("Exit")
            val nameWidth = children.maxOfOrNull { it.commandName.length } ?: 0
            val descriptionWidth = terminal.size.width - nameWidth - 7
            val labels = children.associateWith { command ->
                val description = command.help(command.currentContext).lineSequence().first().trim()
                val shortDescription = when {
                    descriptionWidth < 12 -> ""
                    description.length > descriptionWidth -> description.take(descriptionWidth - 1) + "…"
                    else -> description
                }
                command.commandName.padEnd(nameWidth + 2) + gray(shortDescription)
            }
            val selection = terminal.interactiveSelectList {
                title(path.joinToString(" ") { it.commandName })
                entries(labels.values.map { SelectList.Entry(it) })
                addEntry(if (path.size == 1) exit else back)
            }

            if (selection == null || selection == exit && path.size == 1) return
            if (selection == back) {
                path.removeAt(path.lastIndex)
                continue
            }

            val chosen = labels.entries.firstOrNull { it.value == selection }?.key ?: continue
            path.add(chosen)
            if (chosen.registeredSubcommands().isEmpty()) {
                runCommand(path)
                path.removeAt(path.lastIndex)
            }
        }
    }

    private fun runCommand(path: List<CoreCliktCommand>) {
        val command = path.last()
        command.getFormattedHelp()?.let { terminal.println(it) }
        val input = terminal.prompt("Arguments (Enter to run, /back to return)") ?: return
        if (input == "/back") return

        val args = try {
            CommandLineParser.tokenize(input)
        } catch (error: CliktError) {
            terminal.println(error.message ?: "Invalid arguments")
            terminal.prompt("Press Enter to return to the menu")
            return
        }

        val invocation = Uni()
        try {
            invocation.parse(path.drop(1).map { it.commandName } + args)
        } catch (error: CliktError) {
            invocation.echoFormattedHelp(error)
        }
        terminal.prompt("Press Enter to return to the menu")
    }
}
