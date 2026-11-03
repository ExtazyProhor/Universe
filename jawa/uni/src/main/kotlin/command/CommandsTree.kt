package ru.prohor.universe.uni.cli.command

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.rendering.TextColors.yellow

class CommandsTree(private val rootCommand: UniCommand) : UniCommand("commands-tree") {
    override fun help(context: Context) = "prints tree of all uni commands"

    override fun run() {
        printSingle("", "", rootCommand)
        printSubcommands(rootCommand, "")
    }

    private fun printSubcommands(command: CoreCliktCommand, prefix: String) {
        val subcommands = command.registeredSubcommands()
        subcommands.forEachIndexed { index, sub ->
            val isLast = index == subcommands.lastIndex
            val pointer = if (isLast) "└── " else "├── "
            printSingle(prefix, pointer, sub)

            val nextPrefix = prefix + if (isLast) "    " else "│   "
            printSubcommands(sub, nextPrefix)
        }
    }

    private fun printSingle(prefix: String, pointer: String, command: CoreCliktCommand) {
        val hasSubs = command.registeredSubcommands().isNotEmpty()
        val commandColor = if (hasSubs) magenta else green
        val coloredName = commandColor(command.commandName)

        val helpText = command.help(currentContext)
        val coloredHelp = if (helpText.isNotEmpty()) " - " + yellow(helpText) else ""

        echo("$prefix$pointer$coloredName$coloredHelp")
    }
}
