package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import ru.prohor.universe.uni.cli.command.UniCommand
import java.io.File

class RandLine : UniCommand(name = "rand-line") {
    private val file by argument(help = "file from which the line will be selected")
    private val includesEmpty by option("-e", "--includes-empty", help = "disables the filter that selects only non-blank strings").flag()
    private val withNumber by option("-n", "--with-number", help = "prints a line with its number in the file").flag()

    override fun help(context: Context) = "prints a random line from the specified file"

    override fun run() {
        var lines = File(file).readLines()
        if (!includesEmpty)
            lines = lines.filter { it.isNotBlank() }
        if (lines.isEmpty()) return

        val index = lines.indices.random()
        val line = lines[index]

        if (withNumber) {
            echo("${index + 1}: $line")
        } else {
            echo(line)
        }
    }
}
