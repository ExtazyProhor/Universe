package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.path
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.green
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.helper.fslinter.DiffHighlighter
import ru.prohor.universe.uni.cli.helper.fslinter.DiffOp
import ru.prohor.universe.uni.cli.helper.fslinter.DiffPart
import ru.prohor.universe.uni.cli.helper.fslinter.FixMode
import ru.prohor.universe.uni.cli.helper.fslinter.FixPlanner
import ru.prohor.universe.uni.cli.helper.fslinter.Walker
import ru.prohor.universe.uni.cli.util.errorEcho
import java.nio.file.Files
import kotlin.io.path.Path

/**
 * By default, this is a DRY RUN: it only prints the plan, changed parts
 * of each name highlighted in color (red = removed, green = added).
 * Pass --apply to actually rename on disk.
 *
 * Exit code is 1 if --apply was used and at least one rename failed.
 */
class FixCommand : UniCommand(name = "fix") {
    private val directory by argument(help = "directory to fix")
        .path(mustExist = true, canBeFile = false, canBeDir = true, mustBeReadable = true)
        .default(Path("."))
    private val mode by option("-m", "--mode", help = "replacement strategy, SMART or UNDERSCORE (default: SMART)")
        .choice("smart" to FixMode.SMART, "underscore" to FixMode.UNDERSCORE, ignoreCase = true)
        .default(FixMode.SMART)
    private val apply by option(
        "-a", "--apply",
        help = "actually rename on disk. Without this flag, only the plan is printed"
    ).flag()

    override fun help(context: Context) = "rename files and folders whose names fail the `lint` checks"

    override fun run() {
        val entries = Walker.walk(directory) { path, exception ->
            errorEcho("! cannot read $path: ${exception.message}")
        }
        val plan = FixPlanner.plan(entries, mode)

        if (plan.isEmpty()) {
            echo("Nothing to fix")
            return
        }

        echo("Fix plan (${mode.name.lowercase()}), ${plan.size} entries:\n")
        for (item in plan) {
            val oldName = item.entry.path.fileName.toString()
            val (oldParts, newParts) = DiffHighlighter.diff(oldName, item.newName)
            val oldRendered = renderOld(oldParts)
            val newRendered = renderNew(newParts)
            val parentPrefix = item.entry.rel.parent?.let { "$it/" } ?: ""
            echo("  $parentPrefix$oldRendered")
            echo("    -> $parentPrefix$newRendered\n")
        }

        if (!apply) {
            echo("This is a dry run. Pass --apply to actually rename")
            return
        }

        val sortedPlan = plan.sortedByDescending { it.entry.depth }

        data class LogEntry(val from: String, val to: String, val error: String?)

        val logEntries = mutableListOf<LogEntry>()
        var ok = 0
        for (item in sortedPlan) {
            val src = item.entry.path
            val dst = src.resolveSibling(item.newName)
            try {
                Files.move(src, dst)
                logEntries.add(LogEntry(src.toString(), dst.toString(), null))
                ok++
            } catch (e: Exception) {
                logEntries.add(LogEntry(src.toString(), dst.toString(), e.message ?: e.toString()))
                errorEcho("! failed to rename $src: ${e.message}")
            }
        }

        echo("Done: $ok/${logEntries.size} renamed")
        if (ok != logEntries.size) throw ProgramResult(1)
    }

    private fun renderOld(parts: List<DiffPart>): String = parts.joinToString("") {
        if (it.op == DiffOp.REMOVED) red(it.text) else it.text
    }

    private fun renderNew(parts: List<DiffPart>): String = parts.joinToString("") {
        if (it.op == DiffOp.ADDED) green(it.text) else it.text
    }
}
