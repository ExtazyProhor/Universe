package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class LinterCommand : UniCommand() {
    init {
        subcommands(
            LintCommand(),
            FixCommand(),
            ChecksumCommand(),
            CleanCommand(),
        )
    }

    override fun help(context: Context) = "lint file and folder names for macOS and Windows portability"

    override fun run() = Unit
}
