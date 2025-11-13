package ru.prohor.universe.venator.shared

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path

@Service
class CommandExecutor(
    @Value($$"${UNIVERSE_HOME}") universeHome: String,
    @Value($$"${UNIVERSE_WORKSPACE}") universeWorkspace: String,
) {
    val universeHome: Path = Path.of(universeHome)
    val universeWorkspace: Path = Path.of(universeWorkspace)

    fun runCommand(command: List<String>): String {
        val operation = command.filter { !it.contains("-") && !it.contains("/") }.take(2).joinToString(separator = " ")
        if (operation.isBlank())
            throw IllegalArgumentException("Illegal command: ${command.joinToString(separator = " ")}")
        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText().trim()
        // TODO log
        println("=== $operation repository begin ===")
        println(output)
        println("=== $operation repository end ===")

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("$operation failed with exit code: $exitCode")
        }
        return output
    }
}
