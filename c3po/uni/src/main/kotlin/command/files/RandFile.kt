package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.isCommonFile
import java.io.File

class RandFile : UniCommand(name = "rand-file") {
    private val dir by argument(help = "directory in which files will be searched").default(".")
    private val recursive by option("-r", "--recursive", help = "search files recursively").flag()
    private val absPath by option("-a", "--abs-path", help = "prints an absolute path of selected file").flag()

    override fun help(context: Context) = "prints the name of a random file in the specified directory"

    override fun run() {
        val root = File(dir)

        val files = if (recursive) {
            root.walkTopDown().filter { isCommonFile(it) }.toList()
        } else {
            root.listFiles { isCommonFile(it) }?.toList() ?: emptyList()
        }

        if (files.isEmpty()) return
        val file = files.random()

        echo(if (absPath) file.absolutePath else file.name)
    }
}
