package ru.prohor.universe.uni.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors.red

abstract class UniCommand(name: String? = null) : CliktCommand(name) {
    protected fun errorEcho(message: String) {
        echo(
            message = red(message),
            err = true
        )
    }
}
