package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class Files : UniCommand() {
    init {
        subcommands(
            RandLine(),
            RandFile(),
            Rename(),
        )
    }

    override fun help(context: Context): String = "command for interaction with file system"

    override fun run() = Unit
}
