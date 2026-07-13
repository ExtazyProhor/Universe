package ru.prohor.universe.uni.cli.command.crypto

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class Crypto : UniCommand() {
    init {
        subcommands(
            GenerateRsaKeys(),
        )
    }

    override fun help(context: Context) = "commands for working with cryptography"

    override fun run() = Unit
}
