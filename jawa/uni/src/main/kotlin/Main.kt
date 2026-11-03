package ru.prohor.universe.uni.cli

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.CommandsTree

fun main(args: Array<String>) {
    Launcher().run()
    Uni().let { it.subcommands(CommandsTree(it)) }.main(args)
}
