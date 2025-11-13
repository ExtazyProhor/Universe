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
        if (Files.exists(executor.universeHome)) {
            // TODO log.info("Repository exists, pulling changes");
            pullChanges(branch)
        } else {
            // TODO log.info("Repository doesn't exist, cloning");
            cloneRepository(url, branch)
        }
    }

    fun lastCommit(): String {
        return executor.runCommand(
            listOf("git", "-C", repoPath, "log", "-1", "--format=%H"),
            "git log"
        )
    }

    private fun cloneRepository(url: String, branch: String) {
        Files.createDirectories(executor.universeHome.parent)
        executor.runCommand(
            listOf("git", "clone", "--branch", branch, url, repoPath),
            "git clone"
        )
    }

    private fun pullChanges(branch: String) {
        executor.runCommand(
            listOf("git", "-C", repoPath, "pull", "origin", branch),
            "git pull"
        )
    }
}
