package ru.prohor.universe.uni.cli.util

import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.yellow
import ru.prohor.universe.uni.cli.command.UniCommand
import kotlin.system.exitProcess

data class CmdResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int
)

fun UniCommand.errorEcho(message: String) = echo(message = red(message), err = true)

fun UniCommand.debug(isDebug: Boolean, message: String) {
    if (isDebug) echo(yellow("--- $message"))
}

private fun formatOutput(output: String) = output.split("\n").joinToString("\n") { "\t\t$it" }

fun runCommandInteractive(cmd: String): Int {
    val processArgs = listOf("zsh", "-ic") + "$cmd; exit $?"
    val process = ProcessBuilder(processArgs).inheritIO().start()
    return process.waitFor()
}

fun UniCommand.runCommand(vararg cmd: String, debug: Boolean = false): CmdResult {
    return runCommand(cmd.toList(), debug)
}

fun UniCommand.runCommand(cmd: List<String>, debug: Boolean = false): CmdResult {
    debug(debug, "command: '${cmd.joinToString(" ")}'")
    try {
        val process = ProcessBuilder(cmd)
            .redirectErrorStream(false)
            .start()

        val stdout = process.inputStream.bufferedReader().readText()
        val stderr = process.errorStream.bufferedReader().readText()
        val code = process.waitFor()

        val result = CmdResult(stdout, stderr, code)
        debug(debug, "exit code: ${result.exitCode}")
        debug(debug, "stdout:\n${formatOutput(result.stdout)}")
        debug(debug, "stderr:\n${formatOutput(result.stderr)}")
        return result
    } catch (e: Exception) {
        errorEcho(e.message ?: "error with command '${cmd.first()}'")
        exitProcess(1)
    }
}

fun runCommandStreaming(
    cmd: List<String>,
    onLine: (String) -> Unit
): Int {
    val process = ProcessBuilder(cmd)
        .redirectErrorStream(true)
        .start()

    process.inputStream.bufferedReader().forEachLine {
        onLine(it)
    }
    return process.waitFor()
}

fun runCommandStreaming(
    cmd: List<String>,
    onLine: (String) -> Unit,
    onErrorLine: (String) -> Unit
): Int {
    val process = ProcessBuilder(cmd)
        .redirectErrorStream(false)
        .start()

    Thread {
        process.errorStream.bufferedReader().useLines { lines ->
            lines.forEach { onErrorLine(it) }
        }
    }.start()

    process.inputStream.bufferedReader().forEachLine {
        onLine(it)
    }
    return process.waitFor()
}

fun UniCommand.errorOutputRunCommand(vararg cmd: String, debug: Boolean = false) {
    errorOutputRunCommand(cmd.toList(), debug)
}

fun UniCommand.errorOutputRunCommand(cmd: List<String>, debug: Boolean = false) {
    val result = runCommand(cmd, debug)
    if (result.exitCode != 0) {
        errorEcho(result.stderr)
    }
}

fun UniCommand.defaultOutputRunCommand(vararg cmd: String, debug: Boolean = false) {
    defaultOutputRunCommand(cmd.toList(), debug)
}

fun UniCommand.defaultOutputRunCommand(cmd: List<String>, debug: Boolean = false) {
    val result = runCommand(cmd, debug)
    if (result.exitCode == 0) {
        echo(result.stdout)
    } else {
        errorEcho(result.stderr)
    }
}
