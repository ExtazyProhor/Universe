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

    fun runCommand(
        command: List<String>,
        operation: String
    ): String {
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
