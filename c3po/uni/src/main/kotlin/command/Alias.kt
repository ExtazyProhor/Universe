package ru.prohor.universe.uni.cli.command

import ru.prohor.universe.uni.cli.util.runCommand

abstract class Alias(name: String? = null) : UniCommand(name) {
    abstract val requiredCommand: String

    /**
     * if the command uses parameters, you must implement the property along with `by lazy`
     */
    abstract val fullCommand: List<String>

    open fun postHook() = Unit

    final override fun run() {
        if (!requireCommand(requiredCommand)) return

        val result = runCommand(fullCommand)
        if (result.exitCode == 0) {
            echo(result.stdout)
        } else {
            echo(result.stderr, err = true)
        }
        postHook()
    }
}
