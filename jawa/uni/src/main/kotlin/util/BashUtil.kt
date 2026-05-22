package ru.prohor.universe.uni.cli.util

import com.github.ajalt.mordant.rendering.TextColors.red
import kotlin.system.exitProcess

data class CmdResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int
)

fun runCommand(vararg cmd: String): CmdResult {
    return runCommand(cmd.toList())
}

fun runCommand(cmd: List<String>): CmdResult {
    try {
        val process = ProcessBuilder(cmd)
            .redirectErrorStream(false)
            .start()

        val stdout = process.inputStream.bufferedReader().readText()
        val stderr = process.errorStream.bufferedReader().readText()
        val code = process.waitFor()

        return CmdResult(stdout, stderr, code)
    } catch (e: Exception) {
        println(red(e.message ?: "error with command '${cmd.first()}'"))
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
