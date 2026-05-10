package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.mordant.rendering.TextColors.yellow
import ru.prohor.universe.uni.cli.command.UniCommand

class FindDuplicateLines : UniCommand(name = "find-duplicates") {
    override fun help(context: Context) = "finds duplicate lines in the file or in stdin"

    private val inputFile by argument(help = "file from which the lines will be taken. If not specified, lines are taken from stdin")
        .file(mustExist = true).optional()

    override fun run() {
        val reader = inputFile?.bufferedReader() ?: System.`in`.bufferedReader()

        reader.useLines { lines ->
            lines.filter { it.isNotBlank() }
                .groupBy { it }
                .mapValues { it.value.size }
                .filter { it.value > 1 }
                .toList()
                .sortedBy { it.second }
                .forEach { (line, count) ->
                    println(yellow("$count: $line"))
                }
        }
    }
}
