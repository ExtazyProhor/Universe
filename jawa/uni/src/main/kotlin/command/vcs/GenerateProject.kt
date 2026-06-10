package ru.prohor.universe.uni.cli.command.vcs

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import ru.prohor.universe.uni.cli.command.AliasStreaming

class GenerateProject : AliasStreaming("generate") {
    private val directory by argument(help = "path to the directory with the project relative to the current path")
        .file(mustExist = true, canBeFile = false)

    override fun help(context: Context) = "generates IDEA project from arcadia's directory"

    override val fullCommand by lazy {
        listOf(
            "ya",
            "ide",
            "idea",
            "--yt-store",
            "--group-modules=tree",
            "--iml-in-project-root",
            "--with-content-root-modules",
            "--local",
            "--project-root",
            "~/idea_projects/${directory.absoluteFile.normalize().name}",
            directory.toString()
        )
    }
}
