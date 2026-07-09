package ru.prohor.universe.uni.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors.red
import ru.prohor.universe.uni.cli.util.runCommand

abstract class UniCommand(name: String? = null) : CliktCommand(name) {
    protected fun errorEcho(message: String) {
        echo(
            message = red(message),
            err = true
        )
    }

    protected fun errorOutputRunCommand(vararg cmd: String) {
        errorOutputRunCommand(cmd.toList())
    }

    protected fun errorOutputRunCommand(cmd: List<String>) {
        val result = runCommand(cmd)
        if (result.exitCode != 0) {
            errorEcho(result.stderr)
        }
    }

    protected fun defaultOutputRunCommand(vararg cmd: String) {
        defaultOutputRunCommand(cmd.toList())
    }

    protected fun defaultOutputRunCommand(cmd: List<String>) {
        val result = runCommand(cmd)
        if (result.exitCode == 0) {
            echo(result.stdout)
        } else {
            errorEcho(result.stderr)
        }
    }
}
