package ru.prohor.universe.uni.cli.command.string

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class StringCommand : UniCommand(name = "str") {
    init {
        subcommands(
            CaseSwitch(),
        )
    }

    override fun help(context: Context) = "various useful string operations"

    override fun run() = Unit
}
