package ru.prohor.universe.uni.cli.command.mp3

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import ru.prohor.universe.uni.cli.command.Alias

class Probe : Alias() {
    private val file by argument()

    override fun help(context: Context) = "outputs the result of the ffprobe command for the specified file in json format"

    override val requiredCommand = "ffprobe"

    override val fullCommand by lazy {
        listOf(
            "ffprobe",
            "-v", "quiet",
            "-print_format", "json",
            "-show_format",
            file
        )
    }
}
