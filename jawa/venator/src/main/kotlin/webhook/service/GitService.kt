package ru.prohor.universe.venator.webhook.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path


@Service
class GitService(
    @Value($$"${UNIVERSE_HOME}") universeHome: String
) {
    private val universePath = Path.of(universeHome)

    fun cloneOrPullRepository(url: String, branch: String) {
        if (Files.exists(universePath)) {
            // TODO log.info("Repository exists, pulling changes");
            pullChanges(branch)
        } else {
            // TODO log.info("Repository doesn't exist, cloning");
            cloneRepository(url, branch)
        }
    }

    fun lastCommit(): String {
        return runCommand(listOf("git", "log", "-1", "--format=%H"), "Log")
    }

    private fun cloneRepository(url: String, branch: String) {
        Files.createDirectories(universePath.parent)
        runCommand(listOf("git", "clone", "--branch", branch, url, universePath.toString()), "Clone")
    }

    private fun pullChanges(branch: String) {
        runCommand(listOf("git", "pull", "origin", branch), "Pull", universePath)
    }

    private fun runCommand(
        command: List<String>,
        operation: String,
        workingDir: Path = Path.of(".")
    ):String {
        val process = ProcessBuilder(command)
            .directory(workingDir.toFile())
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
