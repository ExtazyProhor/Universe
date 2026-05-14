package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import ru.prohor.universe.uni.cli.command.UniCommand

abstract class ProcessFileLinesCommand(
    inputFileDescription: String
) : UniCommand() {

    private val inputFile by option("-i", "--input", help = inputFileDescription)
        .file(mustExist = true, canBeDir = false)
        .required()
    private val outputFile by option("-o", "--output", help = "output file, if missing - overwrites input file")
        .file(canBeDir = false)

    abstract fun List<String>.processLines(): List<String>

    override fun run() {
        (outputFile ?: inputFile).writeText(inputFile.readLines().processLines().joinToString("\n"))
    }
}
