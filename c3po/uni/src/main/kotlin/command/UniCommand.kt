package ru.prohor.universe.uni.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors.red
import ru.prohor.universe.uni.cli.util.isCommandAvailable

abstract class UniCommand(name: String? = null) : CliktCommand(name) {
    protected fun requireCommand(cmd: String): Boolean {
        if (isCommandAvailable(cmd)) return true
        errorEcho("Command '$cmd' not found")
        return false
    }

    protected fun errorEcho(message: String) {
        echo(
            message = red(message),
            err = true
        )
    }
}
