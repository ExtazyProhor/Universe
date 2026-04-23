package ru.prohor.universe.uni.cli.command.vcs

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.mordant.terminal.prompt
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.colored
import ru.prohor.universe.uni.cli.util.runCommand

class CleanupBranches : UniCommand(name = "cleanup-branches") {
    private val vcs by argument(help = "one of supported vcs: (${VcsType.entries.joinToString { it.command }})").enum<VcsType>()

    override fun help(context: Context) = "interactively deletes branches merged into the main branch"

    override fun run() {
        val branches = getMergedBranches(vcs)

        for (branch in branches) {
            colored {
                val answer = terminal.prompt("Delete branch ".yellow + branch.purple + "? (y to delete / Enter to skip)".yellow)
                if (answer.equals("y", ignoreCase = true)) {
                    deleteBranch(vcs, branch)
                }
            }
        }
    }

    private fun getMergedBranches(vcs: VcsType): List<String> {
        val cmd = arrayOf(vcs.command, "branch", "--merged", vcs.mainBranch)
        val result = runCommand(*cmd)

        return result.stdout
            .lines()
            .map { it.trim() }
            .filter {
                it.isNotBlank() &&
                    it != vcs.mainBranch &&
                    !it.contains("*")
            }
    }

    private fun deleteBranch(vcs: VcsType, branch: String) {
        val cmd = arrayOf(vcs.command, "branch", "-d", branch)
        val result = runCommand(*cmd)

        if (result.exitCode == 0) {
            echo(result.stdout, false)
        } else {
            errorEcho(result.stderr)
        }
    }
}
