package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context

class Shuffle : ProcessFileLinesCommand("file whose lines will be shuffled") {
    override fun help(context: Context) = "shuffles input file content by lines"

    override fun List<String>.processLines() = shuffled()
}
