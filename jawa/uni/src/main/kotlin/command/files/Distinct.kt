package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context

class Distinct : ProcessFileLinesCommand("file from which duplicate lines will be removed") {
    override fun help(context: Context) = "removes duplicate lines while maintaining their order"

    override fun List<String>.processLines() = distinct()
}
