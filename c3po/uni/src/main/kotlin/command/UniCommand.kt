package ru.prohor.universe.uni.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import ru.prohor.universe.uni.cli.util.colored
import ru.prohor.universe.uni.cli.util.isCommandAvailable

abstract class UniCommand(name: String? = null) : CliktCommand(name) {
    protected fun requireCommand(cmd: String): Boolean {
        if (isCommandAvailable(cmd)) return true
        errorEcho("Command '$cmd' not found")
        return false
    }

    protected fun errorEcho(message: String) {
        colored {
            echo(
                message = message.red,
                err = true
            )
        }
    }
}
