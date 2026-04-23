package ru.prohor.universe.uni.cli.command.mp3

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class Mp3 : UniCommand() {
    init {
        subcommands(
            Probe(),
            Tags(),
            Clear(),
        )
    }

    override fun help(context: Context) = "interacts with mp3 and other music files"

    override fun run() = Unit
}
