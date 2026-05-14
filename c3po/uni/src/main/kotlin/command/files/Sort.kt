package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context

class Sort : ProcessFileLinesCommand("file whose lines will be sorted") {
    override fun help(context: Context) = "sorts input file content by lines"

    override fun List<String>.processLines() = sorted()
}
