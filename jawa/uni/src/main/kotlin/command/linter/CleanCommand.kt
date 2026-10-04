package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.helper.fslinter.Walker
import ru.prohor.universe.uni.cli.util.errorEcho
import java.nio.file.Files
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively

/**
 * By default, this is a DRY RUN: it only lists what would be removed.
 * Pass --apply to actually delete. A junk folder is removed recursively
 * as a whole; its contents are not listed separately.
 * Exit code is 1 if --apply was used and at least one deletion failed.
 */
class CleanCommand : UniCommand(name = "clean") {
    private val directory by argument(help = "directory to clean")
        .path(mustExist = true, canBeFile = false, canBeDir = true, mustBeReadable = true)
        .default(Path("."))
    private val apply by option("-a", "--apply", help = "actually delete, without this flag only a listing is printed")
        .flag()

    override fun help(context: Context) = "find and optionally delete OS-generated junk files and folders"

    @OptIn(ExperimentalPathApi::class)
    override fun run() {
        val targets = Walker.findGarbage(directory) { path, exception ->
            errorEcho("! cannot read $path: ${exception.message}")
        }
        if (targets.isEmpty()) {
            echo("No junk files found")
            return
        }

        echo("Found ${targets.size} junk entries:\n")
        for (e in targets) echo("  ${e.rel}")

        if (!apply) {
            echo("\nThis is a dry run. Pass --apply to delete")
            return
        }

        val sorted = targets.sortedByDescending { it.depth }
        var removed = 0
        var failed = 0
        for (e in sorted) {
            try {
                if (e.isDir) e.path.deleteRecursively() else Files.delete(e.path)
                removed++
            } catch (ex: Exception) {
                failed++
                errorEcho("! failed to delete ${e.path}: ${ex.message}")
            }
        }

        echo("\nRemoved: $removed, failed: $failed.")
        if (failed > 0) throw ProgramResult(1)
    }
}
