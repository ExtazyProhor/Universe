package ru.prohor.universe.venator.shared

data class CommandResult(
    val exitCode: Int,
    val stdout: List<String>,
    val stderr: List<String>
) {
    fun fullOutput(): List<String> {
        return stdout + stderr
    }
}
