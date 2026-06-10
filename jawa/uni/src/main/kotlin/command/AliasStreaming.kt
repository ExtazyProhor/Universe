package ru.prohor.universe.uni.cli.command

import ru.prohor.universe.uni.cli.util.runCommandStreaming

abstract class AliasStreaming(name: String? = null) : UniCommand(name) {
    /**
     * if the command uses parameters, you must implement the property along with `by lazy`
     */
    abstract val fullCommand: List<String>

    override fun run() {
        runCommandStreaming(
            cmd = fullCommand,
            onLine = { echo(it) },
            onErrorLine = { echo(it, err = true) },
        )
    }
}
