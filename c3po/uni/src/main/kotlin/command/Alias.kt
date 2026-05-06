package ru.prohor.universe.uni.cli.command

import ru.prohor.universe.uni.cli.util.runCommand

abstract class Alias(name: String? = null) : UniCommand(name) {
    /**
     * if the command uses parameters, you must implement the property along with `by lazy`
     */
    abstract val fullCommand: List<String>

    override fun run() {
        val result = runCommand(fullCommand)
        if (result.exitCode == 0) {
            echo(result.stdout)
        } else {
            echo(result.stderr, err = true)
        }
    }
}
