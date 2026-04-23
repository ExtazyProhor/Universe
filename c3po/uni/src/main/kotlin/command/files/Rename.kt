package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.isCommonFile
import java.io.File

class Rename : UniCommand() {
    private val search by argument(help = "regexp to search for files")
    private val replace by argument(help = "replacement string")
    private val dir by argument(help = "directory in which files will be searched").default(".")
    private val recursive by option("-r", "--recursive", help = "search files recursively").flag()
    private val dryRun by option("-d", "--dry-run", help = "prints the result without renaming files").flag()

    override fun help(context: Context) = "renames all files in the specified directory using the passed regexp"

    override fun run() {
        val regex = Regex(search)
        val root = File(dir)

        val files = if (recursive) {
            root.walkTopDown().filter { isCommonFile(it) }
        } else {
            root.listFiles()?.asSequence()?.filter { isCommonFile(it) } ?: emptySequence()
        }

        files.forEach { file ->
            val newName = file.name.replace(regex, replace)

            if (file.name != newName) {
                val newFile = File(file.parentFile, newName)

                if (dryRun) {
                    if (recursive) {
                        echo("${file.path} -> ${newFile.path}")
                    } else {
                        echo("${file.name} -> $newName")
                    }
                } else {
                    file.renameTo(newFile)
                }
            }
        }
    }
}
