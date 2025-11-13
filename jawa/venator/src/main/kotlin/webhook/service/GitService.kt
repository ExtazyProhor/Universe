package ru.prohor.universe.venator.webhook.service

import org.springframework.stereotype.Service
import ru.prohor.universe.venator.shared.CommandExecutor
import java.nio.file.Files


@Service
class GitService(
    private val executor: CommandExecutor
) {
    private val repoPath = executor.universeHome.toString()

    fun pullRepository(branch: String) {
        if (!Files.exists(executor.universeHome))
            throw RuntimeException("repository does not exist, clone it")
        pullChanges(branch)
    }

    fun lastCommit(): String {
        return executor.runCommand(
            command = listOf("git", "-C", repoPath, "log", "-1", "--format=%H")
        )
    }

    private fun pullChanges(branch: String) {
        executor.runCommand(
            command = listOf("git", "-C", repoPath, "pull", "origin", branch),
        )
    }
}
