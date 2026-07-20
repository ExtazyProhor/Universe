package ru.prohor.universe.uni.cli.command.vcs

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.runCommandInteractive

class Bisect : UniCommand(name = "bisect") {
    private val lastSuccessRevision by option("--from", "-f", help = "last known successful revision").required()
    private val firstFailRevision by option("--to", "-t", help = "first known failing revision").required()
    private val command by option("--command", "-c", help = "testing command to execute at each step").required()

    override fun help(context: Context) = "finds the culprit revision by binary search"

    override fun run() {
        var from = lastSuccessRevision.parseRevision()
        var to = firstFailRevision.parseRevision()
        if (to <= from) throw IllegalArgumentException("the target revision must be greater than the starting revision")

        var rev: Long
        while (from + 1 < to) {
            rev = (from + to) / 2
            if (isRevisionSuccess(rev)) {
                from = rev
            } else {
                to = rev
            }
        }

        echo("first bad revision is r$to")
    }

    private fun isRevisionSuccess(rev: Long): Boolean {
        runCommandInteractive("arc co r$rev")

        if (command.isBlank()) {
            throw IllegalArgumentException("command cannot be empty")
        }

        val cmd = "$command r$rev"
        val exitCode = runCommandInteractive(cmd)
        return exitCode == 0
    }

    private fun String.parseRevision(): Long {
        return removePrefix("r").toUIntOrNull()?.toLong()
            ?: throw IllegalArgumentException("Invalid revision format: $this. Expected format like 'r123'")
    }
}
