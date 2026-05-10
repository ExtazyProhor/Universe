package ru.prohor.universe.uni.cli.command.video

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class Video : UniCommand() {
    init {
        subcommands(
            CutFragment(),
            ConvertToMp4(),
            Streams(),
        )
    }

    override fun help(context: Context) = "interacts with video files"

    override fun run() = Unit
}
