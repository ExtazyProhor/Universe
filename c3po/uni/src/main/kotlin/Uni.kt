package ru.prohor.universe.uni.cli

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.command.files.Files
import ru.prohor.universe.uni.cli.command.mp3.Mp3
import ru.prohor.universe.uni.cli.command.vcs.Vcs
import ru.prohor.universe.uni.cli.command.video.Video

class Uni : UniCommand() {
    init {
        subcommands(
            Files(),
            Mp3(),
            Video(),
            Vcs(),
        )
    }

    override fun help(context: Context) = "a complex cli utility with a set of various useful functions and tools for the universe"

    override fun run() = Unit
}
