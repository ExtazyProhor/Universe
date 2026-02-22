package ru.prohor.universe.venator.shared

import org.springframework.stereotype.Service
import java.io.InputStream
import kotlin.sequences.forEach

@Service
class CommandExecutor() {
    fun runCommand(command: List<String>): CommandResult {
        val operation = command.filter { !it.contains("-") && !it.contains("/") }.take(2).joinToString(separator = " ")
        if (operation.isBlank())
            throw IllegalArgumentException("Illegal command: ${command.joinToString(separator = " ")}")
        // TODO log $operation started

        val process = ProcessBuilder(command).start()
        val stdout = mutableListOf<String>()
        val stderr = mutableListOf<String>()

        val stdoutThread = makeReaderThread(process.inputStream, stdout)
        val stderrThread = makeReaderThread(process.errorStream, stderr)

        stdoutThread.start()
        stderrThread.start()

        val exitCode = process.waitFor()

        stdoutThread.join()
        stderrThread.join()

        return CommandResult(
            exitCode = exitCode,
            stdout = stdout,
            stderr = stderr
        )
    }

    private fun makeReaderThread(stream: InputStream, appender: MutableList<String>): Thread {
        return Thread {
            stream.bufferedReader().useLines { lines ->
                lines.forEach { appender += it }
            }
        }
    }
}
