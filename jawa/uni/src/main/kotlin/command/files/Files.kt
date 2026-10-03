package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class Files : UniCommand() {
    init {
        subcommands(
            RandFile(),
            Rename(),
            FindDuplicateFiles(),
            CompareDirectories(),
            TreeSize(),
        )
    }

    override fun help(context: Context) = "interacts with multiple files and directories"

    override fun run() = Unit
}
