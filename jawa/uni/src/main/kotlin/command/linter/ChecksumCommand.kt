package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.helper.fslinter.Checksum
import ru.prohor.universe.uni.cli.helper.fslinter.ChecksumNode
import kotlin.io.path.Path

class ChecksumCommand : UniCommand(name = "checksum") {
    override fun help(context: Context) = "compute a recursive content checksum tree for a directory"

    private val directory by argument(help = "directory to hash")
        .path(mustExist = true, canBeFile = false, canBeDir = true, mustBeReadable = true)
        .default(Path("."))
    // TODO убрать и сделать как в команде tree - параметр "L", который показывает какой уровень последний вывести
    private val quiet by option(
        "--quiet",
        help = "print only the root hash, not the full tree"
    ).flag()

    override fun run() {
        val result = Checksum.build(directory)

        if (!quiet) {
            printTree(result.tree, 0)
            echo("")
        }
        echo("ROOT HASH: ${result.rootHash}")
    }

    private fun printTree(nodes: List<ChecksumNode>, indent: Int) {
        for (n in nodes) {
            // TODO выделить цветом
            val marker = if (n.isDir) "[dir] " else "[file]"
            echo("${"  ".repeat(indent)}$marker ${n.name}  [${n.hash.take(12)}]")
            n.children?.let { printTree(it, indent + 1) }
        }
    }
}
