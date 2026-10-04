package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
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
        "-g", "--max-per-group",
        help = "maximum number of examples printed per issue group (default: 10)"
    ).int().default(10)
    private val pathPrefixLength by option(
        "--path-prefix-length",
        help = "length of the future destination folder path, added when checking the " +
                "260-character Windows path limit (default: 0)"
    ).int().default(0)

    override fun help(context: Context) = "scan a directory tree and report file/folder naming problems"

    override fun run() {
        val entries = Walker.walk(directory) { path, exception ->
            errorEcho("! cannot read $path: ${exception.message}")
        }

        val problems = linkedMapOf<IssueCode, MutableList<Pair<FileSystemEntry, Issue>>>()
        for (e in entries) {
            for (issue in NameRules.checkName(e.path.fileName.toString())) {
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

        // TODO мб покрасить в красный
        for ((code, items) in problems) {
            echo("[$code] ${items.size}")
            for ((e, issue) in items.take(maxPerGroup)) {
                echo("    ${e.rel}  --  ${issue.message}")
            }
            if (items.size > maxPerGroup) echo("    ... ${items.size - maxPerGroup} more")
            echo("")
        }

        if (dupGroups.isNotEmpty()) {
            echo("[DUPLICATE_NAME_CASEFOLD] ${dupGroups.size} group(s)")
            for (group in dupGroups.take(maxPerGroup)) {
                echo("    in folder ${group.first().path.parent}:")
                for (e in group) echo("        ${e.path.fileName}")
            }
            echo("")
        }

        if (longPaths.isNotEmpty()) {
            echo("[PATH_TOO_LONG] ${longPaths.size} (limit ${NameRules.WARN_TOTAL_PATH_CHARS} chars)")
            for ((e, totalLen) in longPaths.take(maxPerGroup)) {
                echo("    ${e.rel}  --  $totalLen chars")
            }
            echo("")
        }

        throw ProgramResult(1)
    }
}
