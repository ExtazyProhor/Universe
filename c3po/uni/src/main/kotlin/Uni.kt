package ru.prohor.universe.uni.cli

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.command.files.Files

class Uni : UniCommand() {
    init {
        subcommands(
            Files(),
        )
    }

    override fun help(context: Context): String  = "complex cli utility with a set of various useful functions and tools for the universe"

    override fun run() = Unit
}
