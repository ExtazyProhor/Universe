package ru.prohor.universe.uni.cli.command.mp3

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import ru.prohor.universe.uni.cli.command.Alias

class Clear : Alias() {
    private val file by argument()

    override fun help(context: Context) = "remove ID3 tags"

    override val requiredCommand = "id3v2"

    override val fullCommand by lazy {
        listOf("id3v2", "-D", file)
    }

    override fun run() {
        super.run()
        echo("Tags removed")
    }
}
