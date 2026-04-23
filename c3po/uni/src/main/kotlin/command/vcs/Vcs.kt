package ru.prohor.universe.uni.cli.command.vcs

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class Vcs : UniCommand() {
    init {
        subcommands(
            CleanupBranches(),
        )
    }

    override fun help(context: Context) = "interacts with version control systems"

    override fun run() = Unit
}
