package ru.prohor.universe.uni.cli.command.file

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class FileCommand : UniCommand() {
    init {
        subcommands(
            RandLine(),
            Sort(),
            Shuffle(),
            Distinct(),
            FindDuplicateLines(),
        )
    }

    override fun help(context: Context) = "interacts with single file in the file system"

    override fun run() = Unit
}
