package ru.prohor.universe.uni.cli.command.music

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class Music : UniCommand() {
    init {
        subcommands(
            ConvertToMp3(),
            Tags(),
            Clear(),
        )
    }

    override fun help(context: Context) = "interacts with music files"

    override fun run() = Unit
}
