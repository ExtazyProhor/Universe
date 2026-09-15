package ru.prohor.universe.uni.cli.command.vcs

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.defaultOutputRunCommand
import ru.prohor.universe.uni.cli.util.errorEcho
import ru.prohor.universe.uni.cli.util.runCommand

/**
 * Equivalent to the following zsh function:
 *
 * ```zsh
 * function branch-diff() {
 *     local branch="$1"
 *     arc diff "$(arc merge-base "$branch" trunk)" "$branch"
 * }
 * ```
 */
class BranchDiff : UniCommand(name = "branch-diff") {
    private val branch by argument(help = "branch to compare with trunk from its merge base")
    private val debug by option("--debug", "-d").flag()

    override fun help(context: Context) = "shows diff of the branch from its merge base with trunk"

    override fun run() {
        val mergeBase = runCommand("arc", "merge-base", branch, "trunk", debug = debug).let {
            if (it.exitCode != 0) {
                errorEcho(it.stderr)
                return
            }
            it.stdout.trim()
        }
        defaultOutputRunCommand("arc", "diff", mergeBase, branch, debug = debug)
    }
}
