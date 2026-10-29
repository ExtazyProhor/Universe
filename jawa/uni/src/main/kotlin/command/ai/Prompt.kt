package ru.prohor.universe.uni.cli.command.ai

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.yellow
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.errorEcho
import ru.prohor.universe.uni.cli.util.errorEchoWithExit
import java.io.File

class Prompt : UniCommand("prompt") {
    private val fileName by argument(name = "file-name", help = "file name without '.txt' extension").optional()
    private val listAvailable by option("-l", "--list", help = "show all available prompt files").flag(default = false)

    override fun help(context: Context) = "read prompt file and copy its content via pbcopy"

    override fun run() {
        val baseDir = File(
            System.getenv("UNIVERSE_HOME") ?: errorEchoWithExit("the 'UNIVERSE_HOME' environment variable is missing")
        ).resolve("aiwha/prompts")

        if (listAvailable) {
            if (!baseDir.exists() || !baseDir.isDirectory) {
                errorEchoWithExit("prompt directory not found at: ${baseDir.absolutePath}")
            }

            val files = baseDir.listFiles { file -> file.isFile && file.extension == "txt" }
                ?.map { it.nameWithoutExtension }
                ?.sorted() ?: emptyList()

            if (files.isEmpty()) {
                echo("no prompt files found in ${baseDir.name}")
            } else {
                echo(yellow("available prompts:"))
                files.forEach { echo("  - $it") }
            }
            return
        }

        val name = fileName ?: errorEchoWithExit("missing argument 'file-name' or option '--list'")
        val targetFile = baseDir.resolve("$name.txt")

        if (!targetFile.exists()) {
            errorEchoWithExit("file ${targetFile.name} not found!")
        }

        try {
            val process = ProcessBuilder("pbcopy").redirectInput(targetFile).start()

            val exitCode = process.waitFor()
            if (exitCode == 0) {
                echo("content of '${targetFile.name}' successfully copied to clipboard")
            } else {
                errorEcho("error executing pbcopy, exit code: $exitCode")
            }
        } catch (e: Exception) {
            errorEcho("failed to start pbcopy: ${e.localizedMessage}")
        }
    }
}
