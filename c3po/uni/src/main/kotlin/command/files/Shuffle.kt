package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file

class Shuffle : CliktCommand() {
    private val inputFile by option("-i", "--input", help = "file whose lines will be shuffled")
        .file(mustExist = true, canBeDir = false)
        .required()
    private val outputFile by option("-o", "--output", help = "output file, if missing - overwrites input file")
        .file(canBeDir = false)

    override fun help(context: Context) = "shuffles input file content by lines"

    override fun run() {
        (outputFile ?: inputFile).writeText(inputFile.readLines().shuffled().joinToString("\n"))
    }
}
