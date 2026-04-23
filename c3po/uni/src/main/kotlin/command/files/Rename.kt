package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import ru.prohor.universe.uni.cli.command.UniCommand
import java.io.File

class Rename : UniCommand() {
    private val search by argument(help = "regexp to search for files")
    private val replace by argument(help = "replacement string")
    private val dir by argument(help = "directory in which files will be searched")
    private val dryRun by option("-d", "--dry-run", help = "prints the result of the work without editing the files themselves").flag()

    override fun help(context: Context): String = "renames all files in the specified directory using the passed regexp"

    override fun run() {
        val searchRegex = Regex(search)

        File(dir).listFiles()?.forEach { file ->
            val newName = file.name.replace(searchRegex, replace)

            if (file.name != newName) {
                val newFile = File(file.parentFile, newName)

                if (dryRun) {
                    echo("${file.name} -> $newName")
                } else {
                    file.renameTo(newFile)
                }
            }
        }
    }
}
