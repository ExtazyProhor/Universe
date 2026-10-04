package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.gray
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.yellow
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.helper.fslinter.Duplicates
import ru.prohor.universe.uni.cli.helper.fslinter.FileSystemEntry
import ru.prohor.universe.uni.cli.helper.fslinter.Issue
import ru.prohor.universe.uni.cli.helper.fslinter.IssueCode
import ru.prohor.universe.uni.cli.helper.fslinter.LongPaths
import ru.prohor.universe.uni.cli.helper.fslinter.NameRules
import ru.prohor.universe.uni.cli.helper.fslinter.Walker
import ru.prohor.universe.uni.cli.util.errorEcho
import kotlin.io.path.Path

class LintCommand : UniCommand(name = "lint") {
    private val directory by argument(help = "directory to scan")
        .path(mustExist = true, canBeFile = false, canBeDir = true, mustBeReadable = true)
        .default(Path("."))
    private val maxPerGroup by option(
        "-m", "--max-per-group",
        help = "maximum number of examples printed per issue group (default: 10)"
    ).int().default(10)
    private val pathPrefixLength by option(
        "-p", "--path-prefix-length",
        help = "length of the future destination folder path, added when checking the " +
                "260-character Windows path limit (default: 0)"
    ).int().default(0)
    private val allowNfd by option("--nfd", "--allow-nfd", help = "allow NFD names without reporting NOT_NFC").flag()

    override fun help(context: Context) = "scan a directory tree and report file/folder naming problems"

    override fun run() {
        val entries = Walker.walk(directory) { path, exception ->
            errorEcho("! cannot read $path: ${exception.message}")
        }

        val problems = linkedMapOf<IssueCode, MutableList<Pair<FileSystemEntry, Issue>>>()
        for (e in entries) {
            for (issue in NameRules.checkName(e.path.fileName.toString(), allowNfd = allowNfd)) {
                problems.getOrPut(issue.code) { mutableListOf() }.add(e to issue)
            }
        }

        val dupGroups = Duplicates.findGroups(entries)
        val longPaths = LongPaths.find(entries, pathPrefixLength)

        val totalIssues = problems.values.sumOf { it.size } + dupGroups.size + longPaths.size
        if (totalIssues == 0) {
            echo("No problems found (${entries.size} entries checked).")
            return
        }

        echo("Checked ${entries.size} entries. Found $totalIssues problem(s)\n")

        for ((code, items) in problems) {
            printGroupHeader(code, items.size)
            for ((e, issue) in items.take(maxPerGroup)) {
                val detail = issue.detail?.let { "  —  ${cyan(it)}" }.orEmpty()
                echo("    ${red(e.rel.toString())}$detail")
            }
            if (items.size > maxPerGroup) echo(gray("    ... ${items.size - maxPerGroup} more"))
            echo("")
        }

        if (dupGroups.isNotEmpty()) {
            printGroupHeader(IssueCode.DUPLICATE_NAME_CASEFOLD, dupGroups.size)
            for (group in dupGroups.take(maxPerGroup)) {
                val folder = group.first().rel.parent?.toString() ?: "."
                echo(gray("    In $folder:"))
                for (e in group) echo("        ${red(e.path.fileName.toString())}")
            }
            if (dupGroups.size > maxPerGroup) echo(gray("    ... ${dupGroups.size - maxPerGroup} more"))
            echo("")
        }

        if (longPaths.isNotEmpty()) {
            printGroupHeader(IssueCode.PATH_TOO_LONG, longPaths.size)
            for ((e, totalLen) in longPaths.take(maxPerGroup)) {
                echo("    ${red(e.rel.toString())}  ${cyan("$totalLen chars")}")
            }
            if (longPaths.size > maxPerGroup) echo(gray("    ... ${longPaths.size - maxPerGroup} more"))
            echo("")
        }

        throw ProgramResult(1)
    }

    private fun printGroupHeader(code: IssueCode, count: Int) {
        echo("${yellow("${code.title} ($count)")}  ${gray(code.description)}")
    }
}
