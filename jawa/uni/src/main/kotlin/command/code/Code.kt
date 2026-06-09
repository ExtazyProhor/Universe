package ru.prohor.universe.uni.cli.command.code

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class Code : UniCommand() {
    init {
        subcommands(
            ChangeKotlinPackage(),
        )
    }

    override fun help(context: Context) = "works with files with program code"

    override fun run() = Unit
}
