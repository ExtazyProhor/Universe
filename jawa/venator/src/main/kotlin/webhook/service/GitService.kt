package ru.prohor.universe.venator.webhook.service

import org.springframework.stereotype.Service
import ru.prohor.universe.venator.shared.CommandExecutor
import java.nio.file.Files


@Service
class GitService(
    private val executor: CommandExecutor
) {
    private val repoPath = executor.universeHome.toString()

    fun cloneOrPullRepository(url: String, branch: String) {
        if (!Files.exists(executor.universeHome))
            throw RuntimeException("repository does not exist, clone it")
        pullChanges(branch)
    }

    fun lastCommit(): String {
        return executor.runCommand(
            listOf("git", "-C", repoPath, "log", "-1", "--format=%H"),
            "git log"
        )
    }

    private fun pullChanges(branch: String) {
        executor.runCommand(
            listOf("git", "-C", repoPath, "pull", "origin", branch),
            "git pull"
        )
    }
}
