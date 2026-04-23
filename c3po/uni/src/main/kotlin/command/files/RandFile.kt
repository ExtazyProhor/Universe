package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import ru.prohor.universe.uni.cli.command.UniCommand
import java.io.File

class RandFile : UniCommand(name = "rand-file") {
    private val dir by argument(help = "directory in which files will be searched")
    private val absPath by option("-a", "--abs-path", help = "prints an absolute path of selected file").flag()

    override fun help(context: Context): String = "prints the name of a random file in the specified directory"

    override fun run() {
        val files = File(dir)
            .listFiles { f -> f.isFile && !f.name.startsWith(".") }
            ?.toList()
            ?: return

        if (files.isEmpty()) return
        val file = files.random()

        if (absPath) {
            echo(file.absolutePath)
        } else {
            echo(file.name)
        }
    }
}
