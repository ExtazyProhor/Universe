package ru.prohor.universe.uni.cli.command.ai

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import ru.prohor.universe.uni.cli.command.UniCommand

class AI : UniCommand("ai") {
    init {
        subcommands(
            Prompt(),
        )
    }

    override fun help(context: Context) = "works with ai artifacts, like prompts or skills"

    override fun run() = Unit
}
