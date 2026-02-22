package ru.prohor.universe.venator.webhook.service

import org.springframework.stereotype.Service
import ru.prohor.universe.venator.shared.CommandExecutor
import java.nio.file.Files
import java.nio.file.Path

@Service
class GitService(
    private val executor: CommandExecutor
) {
    fun pullRepository(path: Path, branch: String) {
        if (!Files.exists(path))
            throw RuntimeException("repository does not exist, clone it")
        executor.runCommand(
            command = listOf("git", "-C", path.toString(), "pull", "origin", branch),
        )
    }

    fun lastCommit(path: Path): String {
        return executor.runCommand(
            command = listOf("git", "-C", path.toString(), "log", "-1", "--format=%H")
        ).let {
            if (it.exitCode != 0)
                throw RuntimeException(
                    "Error executing last commit checker: exitCode=${it.exitCode}, stderr:\n${it.stderr}"
                )
            it.stdout.joinToString("\n")
        }
    }
}
