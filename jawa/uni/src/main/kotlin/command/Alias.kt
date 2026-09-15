package ru.prohor.universe.uni.cli.command

import ru.prohor.universe.uni.cli.util.defaultOutputRunCommand

abstract class Alias(name: String? = null) : UniCommand(name) {
    /**
     * if the command uses parameters, you must implement the property along with `by lazy`
     */
    abstract val fullCommand: List<String>

    override fun run() {
        defaultOutputRunCommand(fullCommand)
    }
}
