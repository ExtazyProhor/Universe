package ru.prohor.universe.uni.cli.util

fun isCommandAvailable(cmd: String): Boolean {
    return try {
        ProcessBuilder("which", cmd)
            .redirectErrorStream(true)
            .start()
            .waitFor() == 0
    } catch (_: Exception) {
        false
    }
}

data class CmdResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int
)

fun runCommand(vararg cmd: String): CmdResult {
    val process = ProcessBuilder(*cmd)
        .redirectErrorStream(false)
        .start()

    val stdout = process.inputStream.bufferedReader().readText()
    val stderr = process.errorStream.bufferedReader().readText()
    val code = process.waitFor()

    return CmdResult(stdout, stderr, code)
}
